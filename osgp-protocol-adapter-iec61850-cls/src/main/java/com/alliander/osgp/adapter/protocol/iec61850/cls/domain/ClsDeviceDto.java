package com.alliander.osgp.adapter.protocol.iec61850.cls.domain;

// TODO - Move this class to dto library

public class ClsDeviceDto {
    private String deviceIdentification;
    private String destinationAddress;
    private String gatewayIdentification;
    private boolean keepAlive;
    private long maxIdleTime;
    private long maxSessionTime;
    private byte[] certificate;

    public ClsDeviceDto(final String deviceIdentification, final String destinationAddress,
            final String gatewayIdentification, final boolean keepAlive, final long maxIdleTime,
            final long maxSessionTime, final byte[] certificate) {
        super();
        this.deviceIdentification = deviceIdentification;
        this.destinationAddress = destinationAddress;
        this.gatewayIdentification = gatewayIdentification;
        this.keepAlive = keepAlive;
        this.maxIdleTime = maxIdleTime;
        this.maxSessionTime = maxSessionTime;
        this.certificate = certificate;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public String getDestinationAddress() {
        return this.destinationAddress;
    }

    public String getGatewayIdentification() {
        return this.gatewayIdentification;
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
        return this.certificate;
    }
}
