package com.alliander.osgp.communication.smgwa.client.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.alliander.osgp.communication.smgwa.client.domain.SmgwaClientService;
import com.alliander.osgp.communication.smgwa.client.infra.ZonosSmgwaClientService;

@Configuration
@PropertySource("file:${osp/osgpCommunicationSmgwaClient/config}")
public class SmgwaClientConfig {

    private static final String PROPERTY_NAME_SMGWA_URI = "smgwa.uri";

    @Resource
    private Environment environment;

    private String smgwaUri() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_SMGWA_URI);
    }

    @Bean
    public SmgwaClientService smgwaClientService() {
        return new ZonosSmgwaClientService(this.smgwaUri());
    }
}
