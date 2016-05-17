package com.alliander.osgp.adapter.protocol.iec61850.cls.application.config;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Slf4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking.SmgwServerChannelHandler;
import com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking.SmgwServerPipelineFactory;
import com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking.SmgwServerSslContextFactory;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Configuration
@EnableTransactionManagement()
@PropertySource("file:${osp/osgpAdapterProtocolIec61850Cls/config}")
public class SmgwServerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerConfig.class);

    private static final String PROPERTY_NAME_SMGW_TIMEOUT_CONNECT = "smgw.timeout.connect";
    private static final String PROPERTY_NAME_SMGW_SERVER_PORT = "smgw.server.port";
    private static final String PROPERTY_NAME_SMGW_SERVER_LISTENER_PORT = "smgw.server.listener.port";

    @Resource
    private Environment environment;

    public SmgwServerConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public int connectionTimeout() {
        final int timeout = Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_SMGW_TIMEOUT_CONNECT));
        LOGGER.debug("Bean Connetion Timeout set to {}", timeout);
        return timeout;
    }

    @Bean
    public int smgwPortServer() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_SMGW_SERVER_PORT));
    }

    /**
     * Returns the port the server is listening on for incoming SMGW
     * connections.
     *
     * @return the port number of the SMGW listener endpoint.
     */
    @Bean
    public int smgwServerListenerPort() {
        return Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_SMGW_SERVER_LISTENER_PORT));
    }

    @Bean
    public SmgwServerSslContextFactory smgwServerSslContextFactory() throws TechnicalException {
        return new SmgwServerSslContextFactory();
    }

    @Bean
    public SmgwServerChannelHandler smgwServerChannelHandler() {
        return new SmgwServerChannelHandler();
    }

    @Bean
    public SmgwServerPipelineFactory smgwServerPipelineFactory() {
        return new SmgwServerPipelineFactory();
    }

    /**
     * Returns a ServerBootstrap setting up a server pipeline listening for
     * incoming SMGW connections.
     *
     * @return a SMGW server bootstrap.
     */
    @Bean(destroyMethod = "releaseExternalResources")
    public ServerBootstrap serverBootstrap() {
        final ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(this.smgwServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", false);

        bootstrap.bind(new InetSocketAddress(this.smgwServerListenerPort()));

        return bootstrap;
    }
}
