package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.List;

public class PlatformCommunicationProfile extends CommunicationProfile {
    private SecuritySettings securitySettings;

    public PlatformCommunicationProfile(final String profileName, final List<String> destinationAddresses,
            final TlsSettings tlsSettings, final SecuritySettings securitySettings) {
        super();
        this.profileName = profileName;
        this.destinationAddresses = destinationAddresses;
        this.tlsSettings = tlsSettings;
        this.securitySettings = securitySettings;
    }

    public SecuritySettings getSecuritySettings() {
        return this.securitySettings;
    }

}
