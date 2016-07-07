package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PreDestroy;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.Fc;
import org.openmuc.openiec61850.SclParseException;
import org.openmuc.openiec61850.ServerEventListener;
import org.openmuc.openiec61850.ServerModel;
import org.openmuc.openiec61850.ServerSap;
import org.openmuc.openiec61850.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

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

    @Scheduled(fixedDelay = 60000)
    public void generateData() {
        final Date timestamp = new Date();

        final List<BasicDataAttribute> values = new ArrayList<BasicDataAttribute>(3);
        values.add(this.incrementInt("ZOWN_POCPV1/ZGEN1.OpTmh.stVal", Fc.ST));
        values.add(this.setTime("ZOWN_POCPV1/ZGEN1.OpTmh.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("ZOWN_POCPV1/ZGEN1.GnSpd.mag.f", Fc.MX, 0, 5000));
        values.add(this.setTime("ZOWN_POCPV1/ZGEN1.GnSpd.t", Fc.MX, timestamp));

        values.add(this.incrementInt("ZOWN_POCBATTERY1/ZBAT1.OpTmh.stVal", Fc.ST));
        values.add(this.setTime("ZOWN_POCBATTERY1/ZBAT1.OpTmh.t", Fc.ST, timestamp));

        values.add(this.setRandomFloat("ZOWN_POCBATTERY1/MMXN1.Watt.mag.f", Fc.MX, 0, 1000));
        values.add(this.setTime("ZOWN_POCBATTERY1/MMXN1.Watt.t", Fc.MX, timestamp));

        values.add(this.setRandomFloat("ZOWN_POCBATTERY1/MMXN2.Watt.mag.f", Fc.MX, 0, 550));
        values.add(this.setTime("ZOWN_POCBATTERY1/MMXN2.Watt.t", Fc.MX, timestamp));

        values.add(this.setRandomInt("ZOWN_POCBATTERY1/GGIO1.ISCSO.stVal", Fc.ST, 0, 100));
        values.add(this.setTime("ZOWN_POCBATTERY1/GGIO1.ISCSO.t", Fc.ST, timestamp));

        values.add(this.setRandomInt("ZOWN_POCBATTERY1/MMTR1.TotWh.actVal", Fc.ST, 0, 1000));
        values.add(this.setTime("ZOWN_POCBATTERY1/MMTR1.TotWh.t", Fc.ST, timestamp));
        values.add(this.setRandomFloat("ZOWN_POCBATTERY1/MMTR1.TotWh.pulsQty", Fc.CF, 85, 95));

        values.add(this.setRandomInt("ZOWN_POCBATTERY1/MMTR1.TotVAh.actVal", Fc.ST, 0, 600));
        values.add(this.setTime("ZOWN_POCBATTERY1/MMTR1.TotVAh.t", Fc.ST, timestamp));
        values.add(this.setRandomFloat("ZOWN_POCBATTERY1/MMTR1.TotVAh.pulsQty", Fc.CF, 80, 90));

        this.server.setValues(values);
        LOGGER.info("Generated values");
    }

    private BasicDataAttribute incrementInt(final String node, final Fc fc) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(node, fc);
        value.setValue(value.getValue() + 1);
        return value;
    }

    private BasicDataAttribute setTime(final String node, final Fc fc, final Date date) {
        final BdaTimestamp value = (BdaTimestamp) this.serverModel.findModelNode(node, fc);
        value.setDate(date);
        return value;
    }

    private BasicDataAttribute setRandomFloat(final String node, final Fc fc, final int min, final int max) {
        final BdaFloat32 value = (BdaFloat32) this.serverModel.findModelNode(node, fc);
        value.setFloat((float) ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }

    private BasicDataAttribute setRandomInt(final String node, final Fc fc, final int min, final int max) {
        final BdaInt32 value = (BdaInt32) this.serverModel.findModelNode(node, fc);
        value.setValue(ThreadLocalRandom.current().nextInt(min, max));
        return value;
    }
}
