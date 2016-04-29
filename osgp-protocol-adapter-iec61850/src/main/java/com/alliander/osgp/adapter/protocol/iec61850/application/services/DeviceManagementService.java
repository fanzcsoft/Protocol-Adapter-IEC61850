/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.application.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.messaging.OsgpRequestMessageSender;
import com.alliander.osgp.core.db.api.iec61850.entities.DeviceOutputSetting;
import com.alliander.osgp.core.db.api.iec61850.entities.Ssld;
import com.alliander.osgp.core.db.api.iec61850.repositories.SsldDataRepository;
import com.alliander.osgp.dto.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.EventNotification;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Service(value = "iec61850DeviceManagementService")
@Transactional(value = "iec61850OsgpCoreDbApiTransactionManager")
public class DeviceManagementService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

    @Autowired
    SsldDataRepository ssldDataRepository;

    @Autowired
    private OsgpRequestMessageSender osgpRequestMessageSender;

    public DeviceManagementService() {
        // Parameterless constructor required for transactions...
    }

    /**
     * Send an event notification to OSGP Core.
     *
     * @param deviceIdentification
     *            The identification of the device
     * @param eventNotification
     *            The event notification
     * @throws ProtocolAdapterException
     */
    public void addEventNotification(final String deviceIdentification, final EventNotification eventNotification)
            throws ProtocolAdapterException {

        final Ssld ssldDevice = this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
        if (ssldDevice == null) {
            throw new ProtocolAdapterException("Unable to find device using deviceIdentification: "
                    + deviceIdentification);
        }

        LOGGER.info("addEventNotification called for device {}: {}", deviceIdentification, eventNotification);

        final RequestMessage requestMessage = new RequestMessage("no-correlationUid", "no-organisation",
                deviceIdentification, eventNotification);

        this.osgpRequestMessageSender.send(requestMessage, DeviceFunction.ADD_EVENT_NOTIFICATION.name());
    }

    public List<DeviceOutputSetting> getDeviceOutputSettings(final String deviceIdentification)
            throws ProtocolAdapterException {

        final Ssld ssldDevice = this.ssldDataRepository.findByDeviceIdentification(deviceIdentification);
        if (ssldDevice == null) {
            throw new ProtocolAdapterException("Unable to find device using deviceIdentification: "
                    + deviceIdentification);
        }

        return ssldDevice.getOutputSettings();
    }
}
