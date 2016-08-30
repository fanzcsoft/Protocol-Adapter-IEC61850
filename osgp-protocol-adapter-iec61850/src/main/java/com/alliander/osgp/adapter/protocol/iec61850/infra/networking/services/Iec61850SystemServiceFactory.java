package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.SystemService;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

@Component
public class Iec61850SystemServiceFactory {

    @Autowired
    private Iec61850PvSystemService iec61850PhotovoltaicSystemService;
    @Autowired
    private Iec61850BatterySystemService iec61850BatterySystemService;

    private Map<String, SystemService> systemServices = null;

    public SystemService getSystemService(final SystemFilterDto systemFilter) throws Exception {
        final String key = systemFilter.getSystemType().toUpperCase();
        if (this.getSystemServices().containsKey(key)) {
            return this.getSystemServices().get(key);
        }

        throw new Exception("Invalid System Type in System Filter: [" + key + "]");
    }

    private Map<String, SystemService> getSystemServices() {
        if (this.systemServices == null) {
            this.systemServices = new HashMap<>();
            this.systemServices.put("PV", this.iec61850PhotovoltaicSystemService);
            this.systemServices.put("BATTERY", this.iec61850BatterySystemService);
        }
        return this.systemServices;
    }

}
