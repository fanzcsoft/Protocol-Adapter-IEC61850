package com.alliander.osgp.adapter.protocol.iec61850.cls.infra.networking;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;

public class SmgwServerSslContextFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmgwServerSslContextFactory.class);

    // TODO - Refactor constants to be read from property file and set proper
    // values

    private static final String PROTOCOL = "TLS";
    private static final String PROPERTY_ALGORITHM = "ssl.KeyManagerFactory.algorithm";
    private static final String DEFAULT_ALGORITHM = "SunX509";

    private static final String KEYSTORE_FILE = "/etc/ssl/smgw-server-keystore.jks";
    private static final String KEYSTORE_TYPE = "JKS";
    private static final char[] KEYSTORE_PASSWORD = "secret".toCharArray();

    private static final char[] CERTIFICATE_PASSWORD = "secret".toCharArray();

    private static final String TRUSTSTORE_FILE = "ect/ssl/smgw-server-truststore.jks";
    private static final String TRUSTSTORE_TYPE = "JKS";
    private static final char[] TRUSTSTORE_PASSWORD = "secret".toCharArray();

    private SSLContext sslContext;

    public SmgwServerSslContextFactory() throws TechnicalException {
        this.sslContext = this.initializeContext();
    }

    public SSLContext getServerContext() {
        return this.sslContext;
    }

    private SSLContext initializeContext() throws TechnicalException {
        String algorithm = Security.getProperty(PROPERTY_ALGORITHM);
        if (algorithm == null) {
            algorithm = DEFAULT_ALGORITHM;
        }

        SSLContext context = null;
        InputStream stream = null;

        try {
            // Load keystore
            final KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            stream = new FileInputStream(KEYSTORE_FILE);
            keyStore.load(stream, KEYSTORE_PASSWORD);

            // Set up keymanager factory to use the keystore
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
            keyManagerFactory.init(keyStore, CERTIFICATE_PASSWORD);

            // Load truststore
            final KeyStore trustStore = KeyStore.getInstance(TRUSTSTORE_TYPE);
            stream = new FileInputStream(TRUSTSTORE_FILE);
            trustStore.load(stream, TRUSTSTORE_PASSWORD);

            // Set up truststore factory to use the truststore
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(algorithm);
            trustManagerFactory.init(trustStore);

            // Initialize ssl context
            context = SSLContext.getInstance(PROTOCOL);
            context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        } catch (final KeyManagementException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException
                | CertificateException | IOException e) {
            final String message = "Initialization of SMGW Server SSL Context Factory failed";
            LOGGER.error(message, e);
            throw new TechnicalException(ComponentType.PROTOCOL_IEC61850, message, e);
        }

        return context;
    }
}
