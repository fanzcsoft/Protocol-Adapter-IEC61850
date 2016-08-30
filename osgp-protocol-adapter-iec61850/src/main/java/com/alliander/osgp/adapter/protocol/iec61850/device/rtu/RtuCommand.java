package com.alliander.osgp.adapter.protocol.iec61850.device.rtu;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public interface RtuCommand {
    MeasurementDto execute(Iec61850Client client, DeviceConnection connection, LogicalDevice logicalDevice);
}
