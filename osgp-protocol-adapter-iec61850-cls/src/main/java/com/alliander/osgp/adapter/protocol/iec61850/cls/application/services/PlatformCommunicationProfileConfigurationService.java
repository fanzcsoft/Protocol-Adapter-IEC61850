package com.alliander.osgp.adapter.protocol.iec61850.cls.application.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfile;
import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfileRepository;
import com.alliander.osgp.communication.smgwa.client.domain.SecuritySettings;
import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;
import com.alliander.osgp.communication.smgwa.client.domain.TlsSettings;

@Service
public class PlatformCommunicationProfileConfigurationService {

    // TODO - Refactor
    private static final String PLATFORM_IDENTIFICATION = "osgp-iec61850-cls";
    private static final String DESTINATION_ADDRESS = "127.0.0.1";
    private static final boolean KEEP_ALIVE = false;
    private static final long MAX_IDLE_TIME = 300000;
    private static final long MAX_SESSION_TIME = 1800000;
    private static final byte[] CERTIFICATE = new byte[] {};
    private static final byte[] ENCRYPTION_CERTIFICATE = new byte[] {};
    private static final byte[] SIGNING_CERTIFICATE = new byte[] {};

    @Autowired
    private SmgwaClientService smgwaClientService;

    @Autowired
    private String smgwaPlatformCommunicationProfileName;

    @Autowired
    private PlatformCommunicationProfileRepository platformCommunicationProfileRepository;

    private PlatformCommunicationProfile profile = null;

    @PostConstruct
    public void configure() {

        // Try to retrieve existing profile from repository
        this.profile = this.platformCommunicationProfileRepository
                .getByProfileName(this.smgwaPlatformCommunicationProfileName);
        if (this.profile == null) {
            this.profile = this.createDefaultProfile();
            this.profile = this.platformCommunicationProfileRepository.save(this.profile);
        }

        if (!this.profile.isConfigured()) {
            this.smgwaClientService.configurePlatformCommunicationProfile(PLATFORM_IDENTIFICATION, this.profile);
            this.profile.setConfigurationCompleted();
            this.profile = this.platformCommunicationProfileRepository.save(this.profile);
        }
    }

    private PlatformCommunicationProfile createDefaultProfile() {

        final String profileName = this.smgwaPlatformCommunicationProfileName;

        final List<String> destinationAddresses = new ArrayList<>();
        destinationAddresses.add(DESTINATION_ADDRESS);

        final TlsSettings tlsSettings = new TlsSettings(KEEP_ALIVE, MAX_IDLE_TIME, MAX_SESSION_TIME, CERTIFICATE);

        final SecuritySettings securitySettings = new SecuritySettings(ENCRYPTION_CERTIFICATE, SIGNING_CERTIFICATE);

        return new PlatformCommunicationProfile(profileName, destinationAddresses, tlsSettings, securitySettings);
    }

}
