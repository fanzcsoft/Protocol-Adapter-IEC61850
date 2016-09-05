package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.SystemService;
import com.alliander.osgp.dto.valueobjects.microgrids.SystemFilterDto;

@Component
public class Iec61850SystemServiceFactory {

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
            this.systemServices.put("PV", new Iec61850PvSystemService());
            this.systemServices.put("BATTERY", new Iec61850BatterySystemService());
            this.systemServices.put("RTU", new Iec61850RtuSystemService());
            this.systemServices.put("ENGINE", new Iec61850EngineSystemService());
            this.systemServices.put("LOAD", new Iec61850LoadSystemService());
        }
        return this.systemServices;
    }

}
