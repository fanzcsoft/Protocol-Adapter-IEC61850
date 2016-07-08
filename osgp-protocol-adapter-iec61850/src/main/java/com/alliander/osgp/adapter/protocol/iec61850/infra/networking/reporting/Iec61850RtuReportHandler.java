package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.reporting;

import java.util.List;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementResultSystemIdentifierDto;

public interface Iec61850RtuReportHandler {
    MeasurementResultSystemIdentifierDto createResult(List<MeasurementDto> measurements);

    MeasurementDto handleMember(final ReadOnlyNodeContainer member);
}
