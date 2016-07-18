package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.List;

import org.openmuc.openiec61850.Fc;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.translation.Iec61850BatteryTranslator;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementResultSystemIdentifierDto;

public class Iec61850BatteryReportHandler implements Iec61850RtuReportHandler {

    @Override
    public MeasurementResultSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        final MeasurementResultSystemIdentifierDto systemResult = new MeasurementResultSystemIdentifierDto(1, "BATTERY",
                measurements);
        final List<MeasurementResultSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);
        return systemResult;
    }

    @Override
    public MeasurementDto handleMember(final ReadOnlyNodeContainer member) {
        if (member.getFcmodelNode().getName().equals(DataAttribute.BEHAVIOR.getDescription())) {
            return Iec61850BatteryTranslator.translateBehavior(member);
        }

        if (member.getFcmodelNode().getName().equals(DataAttribute.HEALTH.getDescription())) {
            return Iec61850BatteryTranslator.translateHealth(member);
        }

        if (member.getFcmodelNode().getName().equals(DataAttribute.OPERATIONAL_HOURS.getDescription())) {
            return Iec61850BatteryTranslator.translateOperationTimeInHours(member);
        }

        if (member.getFcmodelNode().getReference().toString().contains(LogicalNode.MEASUREMENT_ONE.getDescription())
                && member.getFcmodelNode().getName().equals(DataAttribute.ACTUAL_POWER.getDescription())) {
            return Iec61850BatteryTranslator.translateActualPowerInput(member);
        }

        if (member.getFcmodelNode().getReference().toString().contains(LogicalNode.MEASUREMENT_TWO.getDescription())
                && member.getFcmodelNode().getName().equals(DataAttribute.ACTUAL_POWER.getDescription())) {
            return Iec61850BatteryTranslator.translateActualPowerOutput(member);
        }

        if (member.getFcmodelNode().getName().equals(DataAttribute.NET_APPARENT_ENERGY.getDescription())
                && member.getFcmodelNode().getFc() == Fc.ST) {
            return Iec61850BatteryTranslator.translateNetApparentEnergy(member);
        }

        if (member.getFcmodelNode().getName().equals(DataAttribute.NET_REAL_ENERGY.getDescription())
                && member.getFcmodelNode().getFc() == Fc.ST) {
            return Iec61850BatteryTranslator.translateNetRealEnergy(member);
        }

        if (member.getFcmodelNode().getName()
                .equals(DataAttribute.INTEGER_STATUS_CONTROLLABLE_STATUS_OUTPUT.getDescription())
                && member.getFcmodelNode().getFc() == Fc.ST) {
            return Iec61850BatteryTranslator.translateIscso(member);
        }
        return null;
    }
}
