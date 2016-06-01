package com.alliander.osgp.adapter.protocol.iec61850.cls.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.communication.smgwa.client.domain.ProxyServer;
import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;

public class ProxyConfigurationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyConfigurationService.class);

    @Autowired
    private SmgwaClientService smgwaClientService;

    @Autowired
    private String platformIdentification;

    @Autowired
    private long sessionTimer;

    public void configure(final String gatewayIdentification, final String deviceIdentification) {
        LOGGER.debug("Configuring proxy server at SMGW [{}] for platform [{}] and device [{}]", gatewayIdentification,
                this.platformIdentification, deviceIdentification);

        final ProxyServer proxy = new ProxyServer(this.platformIdentification, deviceIdentification, this.sessionTimer);

        this.smgwaClientService.configureProxyServer(gatewayIdentification, proxy);
    }

}
