package com.alliander.osgp.communication.smgwa.client.domain;

public interface SmgwaClientService {
    void configurePlatformCommunicationProfile(String platformIdentification, PlatformCommunicationProfile profile);

    void configureDeviceCommunicationProfile(String deviceIdentification, DeviceCommunicationProfile profile);

    void configureProxyServer(String smartmeterGatewayIdentification, ProxyServer proxy);
}
