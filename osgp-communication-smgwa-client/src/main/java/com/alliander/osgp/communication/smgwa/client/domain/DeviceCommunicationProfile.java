package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.List;

public class DeviceCommunicationProfile extends CommunicationProfile {

    private static final long serialVersionUID = -3460902316548835246L;

    private String deviceIdentification;

    @SuppressWarnings("unused")
    private DeviceCommunicationProfile() {
        // Private constructor used by hibernate
    }

    public DeviceCommunicationProfile(final String deviceIdentification, final String profileName,
            final List<String> destinationAddresses, final TlsSettings tlsSettings) {
        super(profileName, destinationAddresses, tlsSettings);
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

}
