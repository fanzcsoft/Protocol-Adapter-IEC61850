package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.SystemService;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.translation.Iec61850PhotovoltaicTranslator;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

public class Iec61850PhotovoltaicSystemService implements SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PhotovoltaicSystemService.class);

    @Override
    public List<MeasurementDto> GetData(final SystemFilterDto systemFilter, final Iec61850Client client,
            final DeviceConnection connection) {

        final List<MeasurementDto> measurements = new ArrayList<>();

        for (final MeasurementFilterDto filter : systemFilter.getMeasurementFilters()) {
            if (filter.getNode().equalsIgnoreCase(DataAttribute.BEHAVIOR.getDescription())) {
                measurements.add(this.getBehavior(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.HEALTH.getDescription())) {
                measurements.add(this.getHealth(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.OPERATIONAL_HOURS.getDescription())) {
                measurements.add(this.getOperationalHours(client, connection));
            } else {
                LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
            }
        }

        return measurements;
    }

    private MeasurementDto getBehavior(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.LOGICAL_NODE_ZERO,
                DataAttribute.BEHAVIOR, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return Iec61850PhotovoltaicTranslator.translateBehavior(containingNode);
    }

    private MeasurementDto getHealth(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.LOGICAL_NODE_ZERO,
                DataAttribute.HEALTH, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return Iec61850PhotovoltaicTranslator.translateHealth(containingNode);
    }

    private MeasurementDto getOperationalHours(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.GENERATOR_ONE,
                DataAttribute.OPERATIONAL_HOURS, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return Iec61850PhotovoltaicTranslator.translateOperationalHours(containingNode);
    }
}
