package com.alliander.osgp.communication.smgwa.client.domain;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;

import com.alliander.osgp.shared.domain.entities.AbstractEntity;

@MappedSuperclass
public abstract class CommunicationProfile extends AbstractEntity {

    private static final long serialVersionUID = 5677658040368957987L;

    @Column(unique = true)
    private String profileName;

    @ElementCollection
    private List<String> destinationAddresses;

    @Embedded
    private TlsSettings tlsSettings;

    @Column
    private boolean configured;

    @Column
    private Date configurationTime;

    protected CommunicationProfile() {
    }

    public CommunicationProfile(final String profileName, final List<String> destinationAddresses,
            final TlsSettings tlsSettings) {
        super();
        this.profileName = profileName;
        this.destinationAddresses = destinationAddresses;
        this.tlsSettings = tlsSettings;
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

    public boolean isConfigured() {
        return this.configured;
    }

    public Date configurationTime() {
        return this.configurationTime;
    }

    public void setConfigurationCompleted() {
        this.configurationTime = new Date();
        this.configured = true;
    }

}