package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmgwServerChannelFutureListener implements ChannelFutureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerChannelFutureListener.class);

    private final SslHandler sslHandler;

    public SmgwServerChannelFutureListener(final SslHandler sslHandler) {
        this.sslHandler = sslHandler;
    }

    @Override
    public void operationComplete(final ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            LOGGER.info("SSL handshake completed, session is protected by {} ciper suite.",
                    this.sslHandler.getEngine().getSession().getCipherSuite());

            // TODO - Add cls command handling via IEC61850 client

        } else {
            LOGGER.info("SSL handshake failed, closing channel.");
            future.getChannel().close();
        }
    }
}
