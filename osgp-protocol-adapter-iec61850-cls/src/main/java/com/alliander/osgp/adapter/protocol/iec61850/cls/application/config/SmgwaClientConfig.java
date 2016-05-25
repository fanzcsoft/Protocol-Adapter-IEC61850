package com.alliander.osgp.adapter.protocol.iec61850.cls.application.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;
import com.alliander.osgp.communication.smgwa.client.infra.ZonosSmgwaClientService;

@Configuration
@PropertySource("file:${osp/osgpAdapterProtocolIec61850Cls/config}")
public class SmgwaClientConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwaClientConfig.class);

    private static final String PROPERTY_NAME_SMGWA_URI = "smgwa.uri";
    private static final String PROPERTY_NAME_SMGWA_PLATFORM_COMMUNICATION_PROFILE_NAME = "smgwa.platform.communication.profile.name";

    @Resource
    private Environment environment;

    @Bean
    public String smgwaPlatformCommunicationProfileName() {
        final String profileName = this.environment
                .getRequiredProperty(PROPERTY_NAME_SMGWA_PLATFORM_COMMUNICATION_PROFILE_NAME);

        LOGGER.debug("Bean SMGWA Platform Communication Profile Name set to: {}", profileName);

        return profileName;
    }

    @Bean
    public SmgwaClientService smgwaClientService() {
        final String uri = this.environment.getRequiredProperty(PROPERTY_NAME_SMGWA_URI);
        final SmgwaClientService service = new ZonosSmgwaClientService(uri);
        LOGGER.debug("Bean SMGWA Client Service set with URI: {}", uri);
        return service;
    }
}
