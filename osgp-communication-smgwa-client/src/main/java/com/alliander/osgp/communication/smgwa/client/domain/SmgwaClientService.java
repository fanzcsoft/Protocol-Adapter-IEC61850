package com.alliander.osgp.communication.smgwa.client.domain;

public interface SmgwaClientService {
    void ConfigurePlatformCommunicationProfile(String PlatformIdentification, PlatformCommunicationProfile profile);

    void ConfigureDeviceCommunicationProfile(String DeviceIdentification, DeviceCommunicationProfile profile);

    void ConfigureProxyServer(String SmartmeterGatewayIdentification, ProxyServer proxy);
}
