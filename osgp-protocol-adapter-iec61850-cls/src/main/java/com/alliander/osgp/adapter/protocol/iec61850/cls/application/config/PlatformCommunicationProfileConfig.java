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
public class PlatformCommunicationProfileConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformCommunicationProfileConfig.class);

    private static final String PROPERTY_NAME_PLATFORM_IDENTIFICATION = "platform.communication.profile.platform.identification";

    private static final String PROPERTY_NAME_SEND_PROFILE_ON_STARTUP = "platform.communication.profile.send.profile.on.startup";
    private static final String PROPERTY_NAME_STORAGE_ENABLED = "platform.communication.profile.storage.enabled";

    private static final String PROPERTY_NAME_PROFILE_NAME = "platform.communication.profile.name";
    private static final String PROPERTY_NAME_DESTINATION_ADDRESS = "platform.communication.profile.destination.address";
    private static final String PROPERTY_NAME_KEEP_ALIVE = "platform.communication.profile.keep.alive";
    private static final String PROPERTY_NAME_MAX_IDLE_TIME = "platform.communication.profile.max.idle.time";
    private static final String PROPERTY_NAME_MAX_SESSION_TIME = "platform.communication.profile.max.session.time";
    private static final String PROPERTY_NAME_PLATFORM_CERTIFICATE_LOCATION = "platform.communication.profile.platform.certificate.location";
    private static final String PROPERTY_NAME_ENCRYPTION_CERTIFICATE_LOCATION = "platform.communication.profile.encryption.certificate.location";
    private static final String PROPERTY_NAME_SIGNING_CERTIFICATE_LOCATION = "platform.communication.profile.signing.certificate.location";

    @Resource
    private Environment environment;

    @Bean
    private String platformIdentification() {
        final String identification = this.environment.getRequiredProperty(PROPERTY_NAME_PLATFORM_IDENTIFICATION);
        LOGGER.debug("Bean Platform Identification set to: {}", identification);
        return identification;
    }

    @Bean
    private boolean sendProfileOnStartup() {
        final boolean flag = Boolean
                .parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_SEND_PROFILE_ON_STARTUP));
        LOGGER.debug("Bean Send profile on startup set to: {}", flag);
        return flag;
    }

    @Bean
    private boolean storageEnabled() {
        final boolean flag = Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_STORAGE_ENABLED));
        LOGGER.debug("Bean Storage Enabled set to: {}", flag);
        return flag;
    }

    @Bean
    public String profileName() {
        final String name = this.environment.getRequiredProperty(PROPERTY_NAME_PROFILE_NAME);
        LOGGER.debug("Bean Profile Name set to: {}", name);
        return name;
    }

    @Bean
    String destinationAddress() {
        final String address = this.environment.getRequiredProperty(PROPERTY_NAME_DESTINATION_ADDRESS);
        LOGGER.debug("Bean Destination Address set to: {}", address);
        return address;
    }

    @Bean
    public boolean keepAlive() {
        final boolean keepAlive = Boolean.parseBoolean(this.environment.getRequiredProperty(PROPERTY_NAME_KEEP_ALIVE));
        LOGGER.debug("Bean Keep Alive set to: {}", keepAlive);
        return keepAlive;
    }

    @Bean
    public long maxIdleTime() {
        final long maxIdleTime = Long.parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_IDLE_TIME));
        LOGGER.debug("Bean Max Idle Time set to: {}", maxIdleTime);
        return maxIdleTime;
    }

    @Bean
    public long maxSessionTime() {
        final long maxSessionTime = Long
                .parseLong(this.environment.getRequiredProperty(PROPERTY_NAME_MAX_SESSION_TIME));
        LOGGER.debug("Bean Max Session Time set to: {}", maxSessionTime);
        return maxSessionTime;
    }

    @Bean
    public String platformCertificateLocation() {
        final String location = this.environment.getRequiredProperty(PROPERTY_NAME_PLATFORM_CERTIFICATE_LOCATION);
        LOGGER.debug("Bean Platform Certificate Location set to: {}", location);
        return location;
    }

    @Bean
    public String encryptionCertificateLocation() {
        final String location = this.environment.getRequiredProperty(PROPERTY_NAME_ENCRYPTION_CERTIFICATE_LOCATION);
        LOGGER.debug("Bean Encryption Certificate Location set to: {}", location);
        return location;
    }

    @Bean
    public String signingCertificateLocation() {
        final String location = this.environment.getRequiredProperty(PROPERTY_NAME_SIGNING_CERTIFICATE_LOCATION);
        LOGGER.debug("Bean SigningCertificateLocation set to: {}", location);
        return location;
    }
}
