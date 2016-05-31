package com.alliander.osgp.adapter.protocol.iec61850.cls.application.config;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("file:${osp/osgpAdapterProtocolIec61850Cls/config}")
public class DeviceCommunicationProfileConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceCommunicationProfileConfig.class);

    private static final String PROPERTY_NAME_CHECK_PROFILES_ON_STARTUP = "device.communication.profile.check.profiles.on.startup";
    private static final String PROPERTY_NAME_PROFILE_NAME_TEMPLATE = "device.communication.profile.name.template";

    @Resource
    private Environment environment;

    @Bean
    private boolean checkDeviceProfilesOnStartup() {
        final boolean flag = Boolean
                .parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_CHECK_PROFILES_ON_STARTUP));
        LOGGER.debug("Bean Check Device Profiles On Startup set to: {}", flag);
        return flag;
    }

    @Bean
    public String profileNameTemplate() {
        final String name = this.environment.getRequiredProperty(PROPERTY_NAME_PROFILE_NAME_TEMPLATE);
        LOGGER.debug("Bean Profile Name Template set to: {}", name);
        return name;
    }

}
