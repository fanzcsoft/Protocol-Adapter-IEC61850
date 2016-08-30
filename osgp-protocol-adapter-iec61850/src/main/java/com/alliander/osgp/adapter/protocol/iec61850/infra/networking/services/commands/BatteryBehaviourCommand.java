package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class BatteryBehaviourCommand implements RtuCommand {

    @Override
    public MeasurementDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice) {
        // TODO Auto-generated method stub
        return null;
    }

}
