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

public class SmgwServerChannelHandler extends SimpleChannelHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerChannelHandler.class);

    // @Autowired
    // private Iec61850LogItemRequestMessageSender
    // iec61850LogItemRequestMessageSender;

    // @Autowired
    // private Iec61850Client iec61850Client;

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

        final SslHandler sslHandler = ctx.getPipeline().get(SslHandler.class);
        final ChannelFuture handshakeFuture = sslHandler.handshake();
        handshakeFuture.addListener(new SmgwServerChannelFutureListener(sslHandler));
        // super.channelConnected(ctx, e);
    };

    @Override
    public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        LOGGER.info("{} Message received: {}", e.getChannel().getId(), e.getMessage());
    }

    // private void logMessage(final RegisterDeviceRequest message) {
    //
    // final Iec61850LogItemRequestMessage iec61850LogItemRequestMessage = new
    // Iec61850LogItemRequestMessage(
    // message.getDeviceIdentification(), true, message.isValid(), message,
    // message.getSize());
    //
    // this.iec61850LogItemRequestMessageSender.send(iec61850LogItemRequestMessage);
    // }

}
