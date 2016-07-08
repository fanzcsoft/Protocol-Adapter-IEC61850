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
        throw new Exception("Invalid System Type in System Filter");
    }

    private Map<String, SystemService> getSystemServices() {
        if (this.systemServices == null) {
            this.systemServices = new HashMap<>();
            this.systemServices.put("PV", new Iec61850PhotovoltaicSystemService());
            this.systemServices.put("BATTERY", new Iec61850BatterySystemService());
            this.systemServices.put("LMGC", new Iec61850LmgcSystemService());
        }
        return this.systemServices;
    }

}
