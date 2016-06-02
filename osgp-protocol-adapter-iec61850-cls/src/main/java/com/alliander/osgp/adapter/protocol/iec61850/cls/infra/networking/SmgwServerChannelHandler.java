/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class SmgwServerChannelHandler extends SimpleChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerChannelHandler.class);

    @Autowired
    private boolean sslHandlerEnabled;

    @Override
    public void channelOpen(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel opened", e.getChannel().getId());
        super.channelOpen(ctx, e);
    }

    @Override
    public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel disconnected", e.getChannel().getId());
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel closed", e.getChannel().getId());
        super.channelClosed(ctx, e);
    }

    @Override
    public void channelUnbound(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel unbound", e.getChannel().getId());
        super.channelUnbound(ctx, e);
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        final int channelId = e.getChannel().getId();
        LOGGER.warn("{} Unexpected exception from downstream. {}", channelId, e.getCause());
        e.getChannel().close();
    }

    @Override
    public void connectRequested(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Connect requested", e.getChannel().getId());
        super.connectRequested(ctx, e);
    };

    @Override
    public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        LOGGER.info("{} Channel connected", e.getChannel().getId());

        if (this.sslHandlerEnabled) {
            final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
            final ChannelFuture handshakeFuture = sslHandler.handshake();
            handshakeFuture.addListener(new SmgwServerChannelFutureListener(sslHandler));
        } else {
            // TODO - Add cls command handling via IEC61850 client
        }
    };

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        LOGGER.info("{} Message received: {}", e.getChannel().getId(), e.getMessage());
    }

}
