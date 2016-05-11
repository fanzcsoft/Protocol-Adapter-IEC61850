package com.alliander.osgp.communication.smgwa.client.infra;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.cxf.jaxrs.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEClsCommProfile;
import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEClsProxy;
import com.alliander.osgp.communication.schemas.zonos.smgwa.TYPEEmtCommProfile;
import com.alliander.osgp.communication.smgwa.client.domain.DeviceCommunicationProfile;
import com.alliander.osgp.communication.smgwa.client.domain.PlatformCommunicationProfile;
import com.alliander.osgp.communication.smgwa.client.domain.ProxyServer;
import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;

import ma.glasnost.orika.MapperFacade;

public class ZonosSmgwaClientService implements SmgwaClientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZonosSmgwaClientService.class);

    @Autowired
    private MapperFacade smgwaClientMapper;

    private String smartMeterGatewayAdministratorUri;

    public ZonosSmgwaClientService(final String smwgaUri) {
        this.smartMeterGatewayAdministratorUri = smwgaUri;
    }

    @Override
    public void ConfigurePlatformCommunicationProfile(final String platformIdentification,
            final PlatformCommunicationProfile profile) {
        final TYPEEmtCommProfile emtCommProfile = this.smgwaClientMapper.map(profile, TYPEEmtCommProfile.class);

        final WebClient client = WebClient.create(this.smartMeterGatewayAdministratorUri);
        client.path(String.format("/v1/emt/{}/comm_profile", platformIdentification));
        client.type(MediaType.APPLICATION_XML_TYPE);

        // TODO - Check correct result status code and add proper exception
        // handling
        final Response response = client.post(emtCommProfile);
        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()
                && response.getStatus() != Status.CREATED.getStatusCode()) {
            LOGGER.error("Unexpected response status: {}", response.getStatus());
        }
    }

    @Override
    public void ConfigureDeviceCommunicationProfile(final String deviceIdentification,
            final DeviceCommunicationProfile profile) {
        final TYPEClsCommProfile clsCommProfile = this.smgwaClientMapper.map(profile, TYPEClsCommProfile.class);

        final WebClient client = WebClient.create(this.smartMeterGatewayAdministratorUri);
        client.path(String.format("/v1/cls/{}/comm_profile", deviceIdentification));
        client.type(MediaType.APPLICATION_XML_TYPE);

        // TODO - Check correct result status code and add proper exception
        // handling
        final Response response = client.post(clsCommProfile);
        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()
                && response.getStatus() != Status.CREATED.getStatusCode()) {
            LOGGER.error("Unexpected response status: {}", response.getStatus());
        }
    }

    @Override
    public void ConfigureProxyServer(final String smartMeterGatewayIdentification, final ProxyServer proxy) {
        final TYPEClsProxy clsProxy = this.smgwaClientMapper.map(proxy, TYPEClsProxy.class);

        final WebClient client = WebClient.create(this.smartMeterGatewayAdministratorUri);
        client.path(String.format("/v1/smgw/{}/cls_proxy", smartMeterGatewayIdentification));
        client.type(MediaType.APPLICATION_XML_TYPE);

        // TODO - Check correct result status code and add proper exception
        // handling
        final Response response = client.post(clsProxy);
        if (response.getStatus() != Status.NO_CONTENT.getStatusCode()
                && response.getStatus() != Status.CREATED.getStatusCode()) {
            LOGGER.error("Unexpected response status: {}", response.getStatus());
        }
    }

}
