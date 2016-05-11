package com.alliander.osgp.communication.smgwa.client.application.mapping;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;

@Component
public class DomainMapper extends ConfigurableMapper {
    @Override
    protected void configure(final MapperFactory factory) {
        super.configure(factory);
        factory.getConverterFactory().registerConverter(new PlatformCommunicationProfileConverter());
        factory.getConverterFactory().registerConverter(new DeviceCommunicationProfileConverter());
    }
}
