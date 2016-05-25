package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

@Component
public class SmgwServerSslContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerSslContextFactory.class);

    private SSLContext sslContext;

    @Autowired
    private String certificatePassword;

    @Autowired
    private KeyStore keyStore;

    @Autowired
    private KeyStore trustStore;

    @Autowired
    private String algorithm;

    @Autowired
    private String protocol;

    public SSLContext getServerContext() {
        return this.sslContext;
    }

    @PostConstruct
    private SSLContext initializeContext() throws TechnicalException {

        SSLContext context = null;

        try {
            // Set up keymanager factory to use the keystore
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(this.algorithm);
            keyManagerFactory.init(this.keyStore, this.certificatePassword.toCharArray());

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
