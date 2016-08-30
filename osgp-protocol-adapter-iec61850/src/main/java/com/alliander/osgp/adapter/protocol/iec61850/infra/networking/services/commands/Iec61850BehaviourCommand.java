package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.Fc;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850BehaviourCommand implements RtuCommand {

    @Override
    public MeasurementDto execute(final Iec61850Client client, final DeviceConnection connection,
            final LogicalDevice logicalDevice) {
        final NodeContainer containingNode = connection.getFcModelNode(logicalDevice, LogicalNode.LOGICAL_NODE_ZERO,
                DataAttribute.BEHAVIOR, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return this.translate(containingNode);
    }

    @Override
    public MeasurementDto translate(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.BEHAVIOR.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

}
