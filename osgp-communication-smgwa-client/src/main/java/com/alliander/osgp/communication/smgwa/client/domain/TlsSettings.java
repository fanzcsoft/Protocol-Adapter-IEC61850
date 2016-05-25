package com.alliander.osgp.communication.smgwa.client.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TlsSettings {
    @Column
    private boolean keepAlive;
    @Column
    private long maxIdleTime;
    @Column
    private long maxSessionTime;
    @Column
    private byte[] certificate;

    @SuppressWarnings("unused")
    private TlsSettings() {
        // Private constructor used by hibernate
    }

    public TlsSettings(final boolean keepAlive, final long maxIdleTime, final long maxSessionTime,
            final byte[] certificate) {
        super();
        this.keepAlive = keepAlive;
        this.maxIdleTime = maxIdleTime;
        this.maxSessionTime = maxSessionTime;
        this.certificate = certificate;
    }

    public boolean isKeepAlive() {
        return this.keepAlive;
    }

    public long getMaxIdleTime() {
        return this.maxIdleTime;
    }

    public long getMaxSessionTime() {
        return this.maxSessionTime;
    }

    public byte[] getCertificate() {
        return this.certificate.clone();
    }

}
