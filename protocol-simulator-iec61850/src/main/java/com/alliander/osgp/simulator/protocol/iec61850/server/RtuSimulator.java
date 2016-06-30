package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerEventListener;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServerSap;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RtuSimulator implements ServerEventListener {

    private final static Logger LOGGER = LoggerFactory.getLogger(RtuSimulator.class);

    private final ServerSap server;

    private final ServerModel serverModel;

    private boolean isStarted = false;

    public RtuSimulator(final int port, final InputStream sclFile) throws SclParseException {
        final List<ServerSap> serverSaps = ServerSap.getSapsFromSclFile(sclFile);
        this.server = serverSaps.get(0);
        this.server.setPort(port);
        this.serverModel = this.server.getModelCopy();

        // Write some value
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode("ZOWN_POCPV1/ZGEN1.GnSpd.mag.f", Fc.MX);
        value.setFloat(4000f);

        final List<BasicDataAttribute> values = new ArrayList<BasicDataAttribute>(3);
        values.add(value);
        this.server.setValues(values);

    }

    public void start() throws IOException {
        if (this.isStarted) {
            throw new IOException("Server is already started");
        }

        this.server.startListening(this);
        this.isStarted = true;
    }

    public void stop() {
        this.server.stop();
        LOGGER.info("Server was stopped.");
    }

    @PreDestroy
    private void destroy() {
        this.stop();
    }

    @Override
    public List<ServiceError> write(final List<BasicDataAttribute> bdas) {
        for (final BasicDataAttribute bda : bdas) {
            LOGGER.info("got a write request: " + bda);
        }

        return null;
    }

    @Override
    public void serverStoppedListening(final ServerSap serverSAP) {
        LOGGER.error("The SAP stopped listening");
    }
}
