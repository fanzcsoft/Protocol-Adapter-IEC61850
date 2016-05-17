package com.alliander.osgp.adapter.protocol.iec61850.cls.application.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
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

    private static final String PROPERTY_NAME_SMGW_SERVER_TIMEOUT_CONNECT = "smgw.server.timeout.connect";
    private static final String PROPERTY_NAME_SMGW_SERVER_LISTENER_PORT = "smgw.server.listener.port";

    private static final String PROPERTY_NAME_SMGW_SERVER_SECURE_SOCKET_PROTOCOL = "smgw.server.secure.socket.protocol";
    private static final String PROPERTY_NAME_SMGW_SERVER_ALGORITHM_PROPERTY = "smgw.server.algorithm.property";
    private static final String PROPERTY_NAME_SMGW_SERVER_ALGORITHM_DEFAULT = "smgw.server.algorithm.default";

    private static final String PROPERTY_NAME_SMGW_SERVER_KEYSTORE_FILE = "smgw.server.keystore.file";
    private static final String PROPERTY_NAME_SMGW_SERVER_KEYSTORE_TYPE = "smgw.server.keystore.type";
    private static final String PROPERTY_NAME_SMGW_SERVER_KEYSTORE_PWD = "smgw.server.keystore.pwd";

    private static final String PROPERTY_NAME_SMGW_SERVER_CERTIFICATE_PWD = "smgw.server.certificate.pwd";

    private static final String PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_FILE = "smgw.server.truststore.file";
    private static final String PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_TYPE = "smgw.server.truststore.type";
    private static final String PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_PWD = "smgw.server.truststore.pwd";

    @Resource
    private Environment environment;

    public SmgwServerConfig() {
        InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
    }

    @Bean
    public int connectionTimeout() {
        final int timeout = Integer.parseInt(this.environment.getProperty(PROPERTY_NAME_SMGW_SERVER_TIMEOUT_CONNECT));
        LOGGER.debug("Bean Connetion Timeout set to {}", timeout);
        return timeout;
    }

    @Bean
    private String algorithm() {
        String algorithm = Security
                .getProperty(this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_ALGORITHM_PROPERTY));
        if (algorithm == null) {
            algorithm = this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_ALGORITHM_DEFAULT);
        }
        return algorithm;
    }

    @Bean
    private char[] certificatePassword() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_CERTIFICATE_PWD).toCharArray();
    }

    @Bean
    private String protocol() {
        return this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_SECURE_SOCKET_PROTOCOL);
    }

    @Bean
    private KeyStore keyStore() {
        InputStream stream = null;
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore
                    .getInstance(this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_KEYSTORE_TYPE));
            stream = new FileInputStream(this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_KEYSTORE_FILE));
            keyStore.load(stream,
                    this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_KEYSTORE_PWD).toCharArray());

        } catch (KeyStoreException | IllegalStateException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            LOGGER.error("Keystore initialization failed.", e);
        }
        return keyStore;
    }

    @Bean
    private KeyStore trustStore() {
        InputStream stream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore
                    .getInstance(this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_TYPE));
            stream = new FileInputStream(
                    this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_FILE));
            trustStore.load(stream,
                    this.environment.getRequiredProperty(PROPERTY_NAME_SMGW_SERVER_TRUSTSTORE_PWD).toCharArray());

        } catch (KeyStoreException | IllegalStateException | NoSuchAlgorithmException | CertificateException
                | IOException e) {
            LOGGER.error("Keystore initialization failed.", e);
        }
        return trustStore;
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
