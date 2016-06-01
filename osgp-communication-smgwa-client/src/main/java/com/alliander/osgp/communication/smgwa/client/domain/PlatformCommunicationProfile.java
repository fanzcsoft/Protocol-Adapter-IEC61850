package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;

@Entity
public class PlatformCommunicationProfile extends CommunicationProfile {
    private static final long serialVersionUID = 7354043697293027789L;

    @Embedded
    private SecuritySettings securitySettings;

    @SuppressWarnings("unused")
    private PlatformCommunicationProfile() {
        // Private constructor used by hibernate
    }

    public PlatformCommunicationProfile(final String profileName, final List<String> destinationAddresses,
            final TlsSettings tlsSettings, final SecuritySettings securitySettings) {
        super(profileName, destinationAddresses, tlsSettings);
        this.securitySettings = securitySettings;
    }

    public SecuritySettings getSecuritySettings() {
        return this.securitySettings;
    }

}
