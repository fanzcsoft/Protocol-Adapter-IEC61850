package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;

import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec61850ClientEventListener implements ClientEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClientEventListener.class);

    private final String deviceIdentification;

    public Iec61850ClientEventListener(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public void newReport(final Report report) {
        LOGGER.info("newReport for device: {}, reportId: {}", this.deviceIdentification, report.getRptId());
    }

    @Override
    public void associationClosed(final IOException e) {
        LOGGER.info("associationClosed for device: {}, {}", this.deviceIdentification, e == null ? "no IOException"
                : "IOException: " + e.getMessage());
    }
}
