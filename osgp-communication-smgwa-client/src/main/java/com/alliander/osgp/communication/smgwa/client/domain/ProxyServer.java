package com.alliander.osgp.communication.smgwa.client.domain;

public class ProxyServer {
    private String platformIdentification;
    private String deviceIdentification;
    private Long sessionTimer;

    public ProxyServer(final String platformIdentification, final String deviceIdentification,
            final Long sessionTimer) {
        this.platformIdentification = platformIdentification;
        this.deviceIdentification = deviceIdentification;
        this.sessionTimer = sessionTimer;
    }

    public String getPlatformIdentification() {
        return this.platformIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public Long getSessionTimer() {
        return this.sessionTimer;
    }

}
