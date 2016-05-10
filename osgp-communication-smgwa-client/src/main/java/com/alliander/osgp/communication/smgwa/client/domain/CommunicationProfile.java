package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.Collections;
import java.util.List;

public abstract class CommunicationProfile {

    protected String profileName;
    protected List<String> destinationAddresses;
    protected TlsSettings tlsSettings;

    public CommunicationProfile() {
        super();
    }

    public String getProfileName() {
        return this.profileName;
    }

    public List<String> getDestinationAddresses() {
        return Collections.unmodifiableList(this.destinationAddresses);
    }

    public TlsSettings getTlsSettings() {
        return this.tlsSettings;
    }

}