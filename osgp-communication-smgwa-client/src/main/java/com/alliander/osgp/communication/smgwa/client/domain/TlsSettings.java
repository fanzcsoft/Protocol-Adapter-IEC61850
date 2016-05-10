package com.alliander.osgp.communication.smgwa.client.domain;

public class TlsSettings {
    private boolean keepAlive;
    private long maxIdleTime;
    private long maxSessionTime;
    private byte[] certificate;

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
