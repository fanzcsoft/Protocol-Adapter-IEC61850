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

public class Iec61850PhotovoltaicSystemService implements SystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850PhotovoltaicSystemService.class);

    @Override
    public List<MeasurementDto> GetData(final SystemFilterDto systemFilter, final Iec61850Client client,
            final DeviceConnection connection) {

        final List<MeasurementDto> measurements = new ArrayList<MeasurementDto>();

        for (final MeasurementFilterDto filter : systemFilter.getMeasurementFilters()) {
            if (filter.getNode().equalsIgnoreCase(DataAttribute.BEHAVIOR.getDescription())) {
                measurements.add(this.getBehavior(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.HEALTH.getDescription())) {
                measurements.add(this.getHealth(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.GENERATOR_SPEED.getDescription())) {
                measurements.add(this.getGenerationSpeed(client, connection));
            } else if (filter.getNode().equalsIgnoreCase(DataAttribute.OPERATIONAL_HOURS.getDescription())) {
                measurements.add(this.getOperationalHours(client, connection));
            } else {
                LOGGER.warn("Unsupported data attribute [{}], skip get data for it", filter.getNode());
            }
        }

        return measurements;
    }

    private MeasurementDto getBehavior(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.GENERATOR_ONE,
                DataAttribute.BEHAVIOR, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.BEHAVIOR.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getHealth(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.GENERATOR_ONE,
                DataAttribute.HEALTH, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());

        return new MeasurementDto(1, DataAttribute.HEALTH.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto getGenerationSpeed(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.GENERATOR_ONE,
                DataAttribute.GENERATOR_SPEED, Fc.MX);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());

        final NodeContainer generationMagnitude = containingNode.getChild(SubDataAttribute.MAGNITUDE);
        return new MeasurementDto(1, DataAttribute.GENERATOR_SPEED.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                generationMagnitude.getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    private MeasurementDto getOperationalHours(final Iec61850Client client, final DeviceConnection connection) {
        final NodeContainer containingNode = connection.getFcModelNode(LogicalDevice.PV, LogicalNode.GENERATOR_ONE,
                DataAttribute.OPERATIONAL_HOURS, Fc.ST);
        client.readNodeDataValues(connection.getConnection().getClientAssociation(), containingNode.getFcmodelNode());
        return new MeasurementDto(1, DataAttribute.OPERATIONAL_HOURS.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.STATE).getValue());
    }

    // @Override
    // public List<MeasurementDto> getAllData() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getBehaviour() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getHealth() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getOperationTimeInHours() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getGeneratorSpeed() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getDemandedPower() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public MeasurementDto getPowerRating() {
    // // TODO Auto-generated method stub
    // return null;
    // }
    //
    // @Override
    // public void setDemandedPower(final SetPointDto setPoint) {
    // // TODO Auto-generated method stub
    //
    // }
    //

}
