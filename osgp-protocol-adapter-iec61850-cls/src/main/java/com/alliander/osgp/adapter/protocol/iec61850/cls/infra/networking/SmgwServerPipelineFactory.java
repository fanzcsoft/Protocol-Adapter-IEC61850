package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.springframework.beans.factory.annotation.Autowired;

public class SmgwServerPipelineFactory implements ChannelPipelineFactory {

    @Autowired
    SmgwServerSslContextFactory smgwServerSslContextFactory;

    @Autowired
    SmgwServerChannelHandler smgwServerChannelHandler;

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        final ChannelPipeline pipeline = Channels.pipeline();

        pipeline.addLast("loggingHandler", new LoggingHandler(InternalLogLevel.INFO, true));

        final SSLEngine engine = this.smgwServerSslContextFactory.getServerContext().createSSLEngine();
        engine.setUseClientMode(false);

        pipeline.addLast("sslHandler", new SslHandler(engine));

        pipeline.addLast("smgwServerChannelHandler", this.smgwServerChannelHandler);

        return pipeline;
    }

}
