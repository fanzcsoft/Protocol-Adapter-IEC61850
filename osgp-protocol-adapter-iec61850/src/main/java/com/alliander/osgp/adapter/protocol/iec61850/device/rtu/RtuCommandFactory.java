package com.alliander.osgp.adapter.protocol.iec61850.device.rtu;

import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

public interface RtuCommandFactory {

    RtuCommand getCommand(final MeasurementFilterDto filter);

    RtuCommand getCommand(final String node);
}
