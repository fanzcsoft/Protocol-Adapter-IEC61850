package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.ArrayList;
import java.util.List;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.translation.Iec61850LmgcTranslator;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementResultSystemIdentifierDto;

public class Iec61850LmgcReportHandler implements Iec61850RtuReportHandler {

    @Override
    public MeasurementResultSystemIdentifierDto createResult(final List<MeasurementDto> measurements) {
        final MeasurementResultSystemIdentifierDto systemResult = new MeasurementResultSystemIdentifierDto(1, "LMGC",
                measurements);
        final List<MeasurementResultSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemResult);
        return systemResult;
    }

    @Override
    public MeasurementDto handleMember(final ReadOnlyNodeContainer member) {
        if (member.getFcmodelNode().getName().equals(DataAttribute.BEHAVIOR.getDescription())) {
            return Iec61850LmgcTranslator.translateBehavior(member);
        }

        if (member.getFcmodelNode().getName().equals(DataAttribute.HEALTH.getDescription())) {
            return Iec61850LmgcTranslator.translateHealth(member);
        }

        // if (member.getFcmodelNode().getName()
        // .equals(DataAttribute.INTEGER_STATUS_CONTROLLABLE_STATUS_OUTPUT.getDescription())
        // && member.getFcmodelNode().getFc() == Fc.ST) {
        // return Iec61850LmgcTranslator.translateIscso(member);
        // }

        return null;
    }
}
