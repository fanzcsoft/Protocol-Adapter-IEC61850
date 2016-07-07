package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.Fc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

public class Iec61850BatterySystemService implements SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850BatterySystemService.class);

    @Override
    public List<MeasurementDto> GetData(final SystemFilterDto systemFilter, final Iec61850Client client,
            final DeviceConnection connection) {

        final List<MeasurementDto> measurements = new ArrayList<MeasurementDto>();

        for (final MeasurementFilterDto filter : systemFilter.getMeasurementFilters()) {

            // TODO refactor
            if (filter.getNode().equalsIgnoreCase(DataAttribute.BEHAVIOR.getDescription())) {
                measurements.add(this.getBehavior(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.HEALTH.getDescription())) {
                measurements.add(this.getHealth(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.OPERATIONAL_HOURS.getDescription())) {
                measurements.add(this.getOperationTimeInHours(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.ACTUAL_POWER.getDescription())) {
                measurements.add(this.getActualPower(client, connection, filter.getId()));
            } else if (filter.getNode()
                    .equalsIgnoreCase(DataAttribute.INTEGER_STATUS_CONTROLLABLE_STATUS_OUTPUT.getDescription())) {
                measurements.add(this.getIscso(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.NET_APPARENT_ENERGY.getDescription())) {
                measurements.add(this.getNetApparentEnergy(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.NET_REAL_ENERGY.getDescription())) {
                measurements.add(this.getNetRealEnergy(client, connection));
            } else {
                LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
            }
        }

        return measurements;
    }

    private MeasurementDto getBehavior(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.BEHAVIOR, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.BEHAVIOR.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getHealth(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.LOGICAL_NODE_ZERO, DataAttribute.HEALTH, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());

        return new MeasurementDto(1, DataAttribute.HEALTH.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getOperationTimeInHours(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY, LogicalNode.BATTERY_ONE,
                DataAttribute.OPERATIONAL_HOURS, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.OPERATIONAL_HOURS.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getActualPower(final Iec61850Client client, final DeviceConnection connection,
            final int id) {
        // TODO Refactor
        switch (id) {
        case 1:
            return this.getActualPowerInput(client, connection);
        case 2:
            return this.getActualPowerOutput(client, connection);
        default:
            LOGGER.error("Actual power request for measurement filter id {} is not supported");
        }
        return null;
    }

    private MeasurementDto getActualPowerInput(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.MEASUREMENT_ONE, DataAttribute.ACTUAL_POWER, Fc.MX);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());

        return new MeasurementDto(1, DataAttribute.ACTUAL_POWER.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getChild(SubDataAttribute.MAGNITUDE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    private MeasurementDto getActualPowerOutput(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.MEASUREMENT_TWO, DataAttribute.ACTUAL_POWER, Fc.MX);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());

        return new MeasurementDto(2, DataAttribute.ACTUAL_POWER.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getChild(SubDataAttribute.MAGNITUDE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    private MeasurementDto getIscso(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.GENERIC_INPUT_OUTPUT_ONE, DataAttribute.INTEGER_STATUS_CONTROLLABLE_STATUS_OUTPUT, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.INTEGER_STATUS_CONTROLLABLE_STATUS_OUTPUT.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getNetApparentEnergy(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.METER_READING_ONE, DataAttribute.NET_APPARENT_ENERGY, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.NET_APPARENT_ENERGY.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.ACTUAL_VALUE).getValue());
    }

    private MeasurementDto getNetRealEnergy(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.BATTERY,
                LogicalNode.METER_READING_ONE, DataAttribute.NET_REAL_ENERGY, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.NET_REAL_ENERGY.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.ACTUAL_VALUE).getValue());
    }

}
