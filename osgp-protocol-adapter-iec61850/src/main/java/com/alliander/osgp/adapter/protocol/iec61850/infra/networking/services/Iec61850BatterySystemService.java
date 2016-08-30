package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.SystemService;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

@Component
public class Iec61850BatterySystemService implements SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BatterySystemService.class);
    private static final String BATTERY = "BATTERY";

    @Override
    public List<MeasurementDto> GetData(final SystemFilterDto systemFilter, final Iec61850Client client,
            final DeviceConnection connection) {

        final List<MeasurementDto> measurements = new ArrayList<>();

        for (final MeasurementFilterDto filter : systemFilter.getMeasurementFilters()) {

            final RtuCommand command = Iec61850BatteryCommandFactory.getInstance().getCommand(filter);
            if (command == null) {
                LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
            } else {
                measurements.add(command.execute(client, connection, this.getLogicalDevice(systemFilter.getId())));
            }

        }

        return measurements;
    }

    private LogicalDevice getLogicalDevice(final int id) {
        return LogicalDevice.fromString(BATTERY + id);
    }
}
