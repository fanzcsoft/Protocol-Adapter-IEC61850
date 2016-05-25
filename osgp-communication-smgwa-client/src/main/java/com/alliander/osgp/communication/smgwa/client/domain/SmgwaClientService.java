package com.alliander.osgp.communication.smgwa.client.domain;

public interface SmgwaClientService {
    void configurePlatformCommunicationProfile(String PlatformIdentification, PlatformCommunicationProfile profile);

    void configureDeviceCommunicationProfile(String DeviceIdentification, DeviceCommunicationProfile profile);

    void configureProxyServer(String SmartmeterGatewayIdentification, ProxyServer proxy);
}
