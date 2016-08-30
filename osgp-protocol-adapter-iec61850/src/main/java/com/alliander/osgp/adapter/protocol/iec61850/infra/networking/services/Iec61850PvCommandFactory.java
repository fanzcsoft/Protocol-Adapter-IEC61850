package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommand;
import com.alliander.osgp.adapter.protocol.iec61850.device.rtu.RtuCommandFactory;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvActualPowerCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvActualPowerLimitCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvBehaviourCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvHealthCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvMaximumPowerLimitCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvModeCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvOperationalHoursCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvStateCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850PvTotalEnergyCommand;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementFilterDto;

public class Iec61850PvCommandFactory implements RtuCommandFactory {

    private static Logger LOGGER = LoggerFactory.getLogger(Iec61850PvCommandFactory.class);

    private static Iec61850PvCommandFactory instance;

    private Map<DataAttribute, RtuCommand> rtuCommandMap = new HashMap<>();

    private Iec61850PvCommandFactory() {
        this.rtuCommandMap.put(DataAttribute.BEHAVIOR, new Iec61850PvBehaviourCommand());
        this.rtuCommandMap.put(DataAttribute.HEALTH, new Iec61850PvHealthCommand());
        this.rtuCommandMap.put(DataAttribute.OPERATIONAL_HOURS, new Iec61850PvOperationalHoursCommand());
        this.rtuCommandMap.put(DataAttribute.MODE, new Iec61850PvModeCommand());
        this.rtuCommandMap.put(DataAttribute.ACTUAL_POWER, new Iec61850PvActualPowerCommand());
        this.rtuCommandMap.put(DataAttribute.MAXIMUM_POWER_LIMIT, new Iec61850PvMaximumPowerLimitCommand());
        this.rtuCommandMap.put(DataAttribute.ACTUAL_POWER_LIMIT, new Iec61850PvActualPowerLimitCommand());
        this.rtuCommandMap.put(DataAttribute.TOTAL_ENERGY, new Iec61850PvTotalEnergyCommand());
        this.rtuCommandMap.put(DataAttribute.STATE, new Iec61850PvStateCommand());
    }

    public static Iec61850PvCommandFactory getInstance() {
        if (instance == null) {
            instance = new Iec61850PvCommandFactory();
        }
        return instance;
    }

    @Override
    public RtuCommand getCommand(final MeasurementFilterDto filter) {

        final RtuCommand command = this.rtuCommandMap.get(DataAttribute.fromString(filter.getNode()));

        if (command == null) {
            LOGGER.warn("No command found for node {}", filter.getNode());
        }
        return command;
    }

}
