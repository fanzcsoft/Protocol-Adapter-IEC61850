package com.alliander.osgp.simulator.protocol.iec61850;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaQuality;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.SclParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alliander.osgp.simulator.protocol.iec61850.client.RtuClient;
import com.alliander.osgp.simulator.protocol.iec61850.server.RtuSimulator;

@SpringBootApplication
public class SimulatorApplication {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimulatorApplication.class);

    public static void main(final String[] args) throws NumberFormatException, IOException {
        // Start client mode
        if (args.length == 3 && args[0].equals("client")) {
            startClient(args[1], Integer.parseInt(args[2]));
            return;
        }

        // Start simulator mode
        if (args.length == 1) {
            startServer(args[0]);
        } else {
            startServer("ZOWN_POC.icd");
        }

        SpringApplication.run(SimulatorApplication.class, args);
    }

    private static void startClient(final String ipAddress, final int port) throws IOException {
        final RtuClient client = new RtuClient(InetAddress.getByName(ipAddress), port);
        client.connect();
        client.readValue("ZOWN_POCPV1/ZGEN1.GnSpd.mag.f", BdaFloat32.class);
        client.readValue("ZOWN_POCPV1/ZGEN1.GnSpd.q", BdaQuality.class);
        client.readValue("ZOWN_POCPV1/ZGEN1.GnSpd.t", BdaTimestamp.class);
        client.disconnect();
    }

    private static void startServer(final String icdFilename) {
        final InputStream icdFile = ClassLoader.getSystemResourceAsStream(icdFilename);

        try {
            final RtuSimulator simulator = new RtuSimulator(12102, icdFile);
            simulator.start();
        } catch (final SclParseException e) {
            LOGGER.warn("Error parsing SCL/ICD file {}", e.getMessage());
        } catch (final IOException e) {
            LOGGER.warn("Failed to start RTU simulator {}", e.getMessage());
        }
    }
}
