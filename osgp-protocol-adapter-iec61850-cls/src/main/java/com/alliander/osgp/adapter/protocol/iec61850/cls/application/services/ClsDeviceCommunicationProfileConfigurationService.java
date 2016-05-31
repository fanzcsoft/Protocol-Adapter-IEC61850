package com.alliander.osgp.adapter.protocol.iec61850.cls.application.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.adapter.protocol.iec61850.cls.domain.ClsDeviceDto;
import com.alliander.osgp.communication.smgwa.client.domain.DeviceCommunicationProfile;
import com.alliander.osgp.communication.smgwa.client.domain.DeviceCommunicationProfileRepository;
import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;
import com.alliander.osgp.communication.smgwa.client.domain.TlsSettings;

public class ClsDeviceCommunicationProfileConfigurationService {
    private static Logger LOGGER = LoggerFactory.getLogger(ClsDeviceCommunicationProfileConfigurationService.class);

    @Autowired
    private SmgwaClientService smgwaClientService;

    @Autowired
    private DeviceCommunicationProfileRepository deviceCommunicationProfileRepository;

    @Autowired
    private boolean checkDeviceProfilesOnStartup;

    @Autowired
    private String profileNameTemplate;

    @PostConstruct
    public void checkDeviceProfiles() {

        if (this.checkDeviceProfilesOnStartup) {

            LOGGER.debug("Checking Device Communication Profiles");

            final List<DeviceCommunicationProfile> profiles = this.deviceCommunicationProfileRepository
                    .getByConfigured(false);

            for (final DeviceCommunicationProfile profile : profiles) {
                this.configureDeviceProfile(profile);
            }
        }
    }

    public void addDeviceCommunicationProfile(final ClsDeviceDto device) {
        DeviceCommunicationProfile profile = this.createProfile(device);
        profile = this.deviceCommunicationProfileRepository.save(profile);

        this.configureDeviceProfile(profile);
    }

    private void configureDeviceProfile(DeviceCommunicationProfile profile) {
        this.smgwaClientService.configureDeviceCommunicationProfile(profile.getDeviceIdentification(), profile);

        profile.setConfigurationCompleted();
        profile = this.deviceCommunicationProfileRepository.save(profile);
    }

    private DeviceCommunicationProfile createProfile(final ClsDeviceDto device) {

        final String profileName = this.profileNameTemplate.replace("<<<deviceid>>>", device.getDeviceIdentification());

        final List<String> destinationAddresses = new ArrayList<>();
        destinationAddresses.add(device.getDestinationAddress());

        final TlsSettings tlsSettings = new TlsSettings(device.isKeepAlive(), device.getMaxIdleTime(),
                device.getMaxSessionTime(), device.getCertificate());

        return new DeviceCommunicationProfile(device.getDeviceIdentification(), profileName, destinationAddresses,
                tlsSettings);
    }

}
