package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.util.List;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

public interface SystemService {
    List<MeasurementDto> GetData(SystemFilterDto systemFilter, final Iec61850Client client,
            final DeviceConnection connection);
}
