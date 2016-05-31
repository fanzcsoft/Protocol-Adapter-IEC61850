package com.alliander.osgp.adapter.protocol.iec61850.cls.domain;

// TODO - Move this class to dto library

public class ClsDeviceDto {
    private String DeviceIdentification;
    private String DestinationAddress;
    private boolean keepAlive;
    private long maxIdleTime;
    private long maxSessionTime;
    private byte[] certificate;

    public ClsDeviceDto(final String deviceIdentification, final String destinationAddress, final boolean keepAlive,
            final long maxIdleTime, final long maxSessionTime, final byte[] certificate) {
        super();
        this.DeviceIdentification = deviceIdentification;
        this.DestinationAddress = destinationAddress;
        this.keepAlive = keepAlive;
        this.maxIdleTime = maxIdleTime;
        this.maxSessionTime = maxSessionTime;
        this.certificate = certificate;
    }

    public String getDeviceIdentification() {
        return this.DeviceIdentification;
    }

    public String getDestinationAddress() {
        return this.DestinationAddress;
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
