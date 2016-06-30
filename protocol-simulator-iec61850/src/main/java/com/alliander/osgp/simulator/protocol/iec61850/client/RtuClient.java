package com.alliander.osgp.simulator.protocol.iec61850.client;

import java.io.IOException;
import java.net.InetAddress;

import javax.annotation.PreDestroy;

import org.openmuc.openiec61850.ClientAssociation;
import org.openmuc.openiec61850.ClientSap;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtuClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(RtuClient.class);

    private final InetAddress ipAddress;

    private final int port;

    private final ClientSap clientSap;

    private ClientAssociation association;

    private ServerModel serverModel;

    public RtuClient(final InetAddress ipAddress, final int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.clientSap = new ClientSap();
    }

    public void connect() throws IOException {

        // Connect and read model
        this.association = this.clientSap.associate(this.ipAddress, this.port, null, null);

        try {
            // RetrieveModel() will call all GetDirectory and GetDefinition ACSI
            // services needed to get the complete server model.
            this.serverModel = this.association.retrieveModel();
            this.association.getAllDataValues();
            LOGGER.info("Client was connected.");
            return;
        } catch (final ServiceError e) {
            LOGGER.error("Service Error requesting model.", e);
            this.association.close();
        } catch (final IOException e) {
            LOGGER.error("Fatal IOException requesting model.", e);
        }
    }

    public void disconnect() {
        this.association.disconnect();
        LOGGER.info("Client was disconnected.");
    }

    public <T> T readValue(final String logicalNode, final Class<T> type) {
        final FcModelNode node = (FcModelNode) this.serverModel.findModelNode(logicalNode, Fc.MX);
        final T typedValue = type.cast(node);
        LOGGER.info("{}: {}", logicalNode, typedValue);
        return typedValue;
    }

    @PreDestroy
    private void destroy() {
        this.disconnect();
    }
}
