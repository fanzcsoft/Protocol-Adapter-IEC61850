package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;

public class Iec61850CommandTranslator {

    public static MeasurementDto translateBehavior(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.BEHAVIOR.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    public static MeasurementDto translateHealth(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.HEALTH.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    public static MeasurementDto translateMode(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.MODE.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    public static MeasurementDto translateActualPower(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.ACTUAL_POWER.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getChild(SubDataAttribute.MAGNITUDE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    // TODO - Maximum Power Limit is not available in ICD for WAGO device,
    public static MeasurementDto translateMaximumPowerLimit(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.MAXIMUM_POWER_LIMIT.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getChild(SubDataAttribute.SUBSTITUDE_VALUE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    public static MeasurementDto translateActualPowerLimit(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.ACTUAL_POWER_LIMIT.getDescription(), 0,
                // new DateTime(containingNode.getDate(SubDataAttribute.TIME),
                // DateTimeZone.UTC),
                new DateTime(),
                containingNode.getChild(SubDataAttribute.SUBSTITUDE_VALUE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    public static MeasurementDto translateTotalEnergy(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.TOTAL_ENERGY.getDescription(), 0,
                // new DateTime(containingNode.getDate(SubDataAttribute.TIME),
                // DateTimeZone.UTC),
                new DateTime(),
                containingNode.getChild(SubDataAttribute.MAGNITUDE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    public static MeasurementDto translateState(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.STATE.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getByte(SubDataAttribute.STATE).getValue());
    }

    public static MeasurementDto translateStateOfCharge(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.STATE_OF_CHARGE.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getChild(SubDataAttribute.MAGNITUDE).getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    public static MeasurementDto translateOperationalHours(final NodeContainer containingNode) {
        return new MeasurementDto(1, DataAttribute.OPERATIONAL_HOURS.getDescription(), 0,
                new DateTime(containingNode.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                containingNode.getInteger(SubDataAttribute.STATE).getValue());
    }
}
