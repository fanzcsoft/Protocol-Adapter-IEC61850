package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class SmgwServerSslContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerSslContextFactory.class);

    private SSLContext sslContext;

    @Autowired
    char[] certificatePassword;

    @Autowired
    KeyStore keyStore;

    @Autowired
    KeyStore trustStore;

    @Autowired
    String algorithm;

    @Autowired
    String protocol;

    public SmgwServerSslContextFactory() throws TechnicalException {
        this.sslContext = this.initializeContext();
    }

    public SSLContext getServerContext() {
        return this.sslContext;
    }

    private SSLContext initializeContext() throws TechnicalException {

        SSLContext context = null;

        try {
            // Set up keymanager factory to use the keystore
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.algorithm);
            keyManagerFactory.init(this.keyStore, this.certificatePassword);

            // Set up truststore factory to use the truststore
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(this.algorithm);
            trustManagerFactory.init(this.trustStore);

            // Initialize ssl context
            context = SSLContext.getInstance(this.protocol);
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        } catch (final KeyManagementException | KeyStoreException | NoSuchAlgorithmException
                | UnrecoverableKeyException e) {
            final String message = "Initialization of SMGW Server SSL Context Factory failed";
            LOGGER.error(message, e);
            throw new TechnicalException(ComponentType.PROTOCOL_IEC61850_CLS, message, e);
        }

        return context;
    }
}
