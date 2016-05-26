package com.alliander.osgp.adapter.protocol.iec61850.cls.application.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfile;
import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfileRepository;
import com.alliander.osgp.communication.smgwa.client.domain.SecuritySettings;
import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;
import com.alliander.osgp.communication.smgwa.client.domain.TlsSettings;

@Service
public class PlatformCommunicationProfileConfigurationService {

    private static Logger LOGGER = LoggerFactory.getLogger(PlatformCommunicationProfileConfigurationService.class);

    @Autowired
    private SmgwaClientService smgwaClientService;

    @Autowired
    private PlatformCommunicationProfileRepository platformCommunicationProfileRepository;

    @Autowired
    private String platformIdentification;

    @Autowired
    private boolean sendProfileOnStartup;

    @Autowired
    private boolean storageEnabled;

    @Autowired
    private String profileName;

    @Autowired
    private String destinationAddress;

    @Autowired
    private boolean keepAlive;

    @Autowired
    private long maxIdleTime;

    @Autowired
    private long maxSessionTime;

    @Autowired
    private String platformCertificateLocation;

    @Autowired
    private String encryptionCertificateLocation;

    @Autowired
    private String signingCertificateLocation;

    @PostConstruct
    public void configure() {

        if (this.sendProfileOnStartup) {
            this.sendProfile();
        }
    }

    private void sendProfile() {
        PlatformCommunicationProfile profile = null;

        try {
            if (this.storageEnabled) {
                profile = this.platformCommunicationProfileRepository.getByProfileName(this.profileName);
            }

            if (profile == null) {
                profile = this.createProfile();
            }
            this.smgwaClientService.configurePlatformCommunicationProfile(this.platformIdentification, profile);

            if (this.storageEnabled) {
                profile.setConfigurationCompleted();
                profile = this.platformCommunicationProfileRepository.save(profile);
            }
        } catch (IllegalStateException | IOException e) {
            LOGGER.error("Error creating Platform Communication Profile", e);
        }

    }

    private PlatformCommunicationProfile createProfile() throws IllegalStateException, IOException {

        final List<String> destinationAddresses = new ArrayList<>();
        destinationAddresses.add(this.destinationAddress);

        final TlsSettings tlsSettings = new TlsSettings(this.keepAlive, this.maxIdleTime, this.maxSessionTime,
                this.loadCertificate(this.platformCertificateLocation));

        final SecuritySettings securitySettings = new SecuritySettings(
                this.loadCertificate(this.encryptionCertificateLocation),
                this.loadCertificate(this.signingCertificateLocation));

        return new PlatformCommunicationProfile(this.profileName, destinationAddresses, tlsSettings, securitySettings);
    }

    private byte[] loadCertificate(final String location) throws IOException {
        LOGGER.debug("Loading certificate from file {}", location);
        final Path path = Paths.get(location);
        return Files.readAllBytes(path);
    }

}
