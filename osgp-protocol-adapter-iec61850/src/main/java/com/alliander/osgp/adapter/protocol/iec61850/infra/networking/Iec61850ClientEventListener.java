package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaInt8U;
import org.openmuc.openiec61850.BdaOptFlds;
import org.openmuc.openiec61850.BdaReasonForInclusion;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.BdaVisibleString;
import org.openmuc.openiec61850.ClientEventListener;
import org.openmuc.openiec61850.DataSet;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.HexConverter;
import org.openmuc.openiec61850.Report;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.dto.valueobjects.EventType;

public class Iec61850ClientEventListener implements ClientEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClientEventListener.class);

    /**
     * The EntryTime from IEC61850 has timestamp values relative to 01-01-1984.
     * TimeStamp values and Java date time values have milliseconds since
     * 01-01-1970. The milliseconds between these representations are in the
     * following offset.
     */
    private static final long IEC61850_ENTRY_TIME_OFFSET = 441763200000L;

    private static final Map<Short, String> EVN_TYPE_DESCRIPTION_PER_CODE = new TreeMap<>();
    private static final Map<Short, EventType> OSGP_EVENT_TYPE_PER_CODE = new TreeMap<>();
    private static final Map<Short, String> TRG_TYPE_DESCRIPTION_PER_CODE = new TreeMap<>();

    /*-
     * OSGP Event types not mapped from IEC61850 evnType:
     *
     * HARDWARE_FAILURE_RELAY(1)
     * LIGHT_FAILURE_DALI_COMMUNICATION(2)
     * LIGHT_FAILURE_BALLAST(3)
     * MONITOR_EVENTS_LONG_BUFFER_FULL(6)
     * LIGHT_FAILURE_TARIFF_SWITCH_ATTEMPT(10)
     * MONITOR_FAILURE_P1_COMMUNICATION(13)
     * COMM_EVENTS_ALTERNATIVE_CHANNEL(14)
     * COMM_EVENTS_RECOVERED_CHANNEL(15)
     * SECURITY_EVENTS_OUT_OF_SEQUENCE(16)
     * MONITOR_SHORT_DETECTED(17)
     * MONITOR_SHORT_RESOLVED(18)
     * MONITOR_DOOR_OPENED(19)
     * MONITOR_DOOR_CLOSED(20)
     * ALARM_NOTIFICATION(21)
     * SMS_NOTIFICATION(22)
     * MONITOR_EVENTS_TEST_RELAY_ON(23)
     * MONITOR_EVENTS_TEST_RELAY_OFF(24)
     * MONITOR_EVENTS_LOCAL_MODE(26)
     * MONITOR_EVENTS_REMOTE_MODE(27)
     * FIRMWARE_EVENTS_CONFIGURATION_CHANGED(28)
     *
     *
     * OSGP Event types mapped from multiple IEC61850 evnType:
     *
     * FIRMWARE_EVENTS_ACTIVATING(7)
     *   from  7 - FUNCTION_FIRMWARE_EVENTS_ACTIVATING
     *   and  11 - SECURITY_FIRMWARE_EVENTS_ACTIVATING
     *
     * FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND(8)
     *   from  8 - FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND
     *   and  12 - SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND
     *
     * FIRMWARE_EVENTS_DOWNLOAD_FAILED(9)
     *   from  9 - FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED
     *   and  13 - SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED
     *
     *
     * IEC61850 evnType without OSGP Event type
     *
     * 10 - FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS
     * 14 - SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS
     * 15 - CA_FILE_EVENTS_ACTIVATING
     * 16 - CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND
     * 17 - CA_FILE_EVENTS_DOWNLOAD_FAILED
     * 18 - CA_FILE_EVENTS_DOWNLOAD_SUCCESS
     * 19 - NTP_SERVER_NOT_REACH
     * 20 - NTP_SYNC_ALARM_OFFSET
     * 21 - NTP_SYNC_MAX_OFFSET
     * 22 - AUTHENTICATION_FAIL
     */
    static {
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 1, "DIAG_EVENTS_GENERAL");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 1, EventType.DIAG_EVENTS_GENERAL);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 2, "LIGHT_EVENTS_LIGHT_ON");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 2, EventType.LIGHT_EVENTS_LIGHT_ON);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 3, "LIGHT_EVENTS_LIGHT_OFF");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 3, EventType.LIGHT_EVENTS_LIGHT_OFF);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 4, "TARIFF_EVENTS_TARIFF_ON");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 4, EventType.TARIFF_EVENTS_TARIFF_ON);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 5, "TARIFF_EVENTS_TARIFF_OFF");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 5, EventType.TARIFF_EVENTS_TARIFF_OFF);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 6, "MONITOR_EVENTS_LOSS_OF_POWER");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 6, EventType.MONITOR_EVENTS_LOSS_OF_POWER);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 7, "FUNCTION_FIRMWARE_EVENTS_ACTIVATING");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 7, EventType.FIRMWARE_EVENTS_ACTIVATING);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 8, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 8, EventType.FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 9, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 9, EventType.FIRMWARE_EVENTS_DOWNLOAD_FAILED);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 10, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 10, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 11, "SECURITY_FIRMWARE_EVENTS_ACTIVATING");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 11, EventType.FIRMWARE_EVENTS_ACTIVATING);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 12, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 12, EventType.FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 13, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 13, EventType.FIRMWARE_EVENTS_DOWNLOAD_FAILED);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 14, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 14, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 15, "CA_FILE_EVENTS_ACTIVATING");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 15, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 16, "CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 16, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 17, "CA_FILE_EVENTS_DOWNLOAD_FAILED");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 17, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 18, "CA_FILE_EVENTS_DOWNLOAD_SUCCESS");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 18, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 19, "NTP_SERVER_NOT_REACH");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 19, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 20, "NTP_SYNC_ALARM_OFFSET");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 20, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 21, "NTP_SYNC_MAX_OFFSET");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 21, null);
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 22, "AUTHENTICATION_FAIL");
        OSGP_EVENT_TYPE_PER_CODE.put((short) 22, null);

        TRG_TYPE_DESCRIPTION_PER_CODE.put((short) 1, "light trigger (sensor trigger)");
        TRG_TYPE_DESCRIPTION_PER_CODE.put((short) 2, "ad-hoc trigger");
        TRG_TYPE_DESCRIPTION_PER_CODE.put((short) 3, "fixed time trigger");
        TRG_TYPE_DESCRIPTION_PER_CODE.put((short) 4, "autonomous trigger");
    }

    private final String deviceIdentification;
    private final DeviceManagementService deviceManagementService;

    public Iec61850ClientEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService) {
        this.deviceIdentification = deviceIdentification;
        this.deviceManagementService = deviceManagementService;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    @Override
    public void newReport(final Report report) {
        final String reportDescription = String.format("device: %s, reportId: %s, timeOfEntry: %s, sqNum: %s%s%s",
                this.deviceIdentification, report.getRptId(), report.getTimeOfEntry() == null ? "-" : new DateTime(
                        report.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET), report.getSqNum(),
                        report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
                                report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
        LOGGER.info("newReport for {}", reportDescription);
        if (report.isBufOvfl()) {
            LOGGER.warn("Buffer Overflow reported for {} - entries within the buffer may have been lost.",
                    reportDescription);
        }
        this.logReportDetails(report);

        final DataSet dataSet = report.getDataSet();
        if (dataSet == null) {
            LOGGER.warn("No DataSet available for {}", reportDescription);
            return;
        }
        final List<FcModelNode> members = dataSet.getMembers();
        if (members == null || members.isEmpty()) {
            LOGGER.warn("No members in DataSet available for {}", reportDescription);
            return;
        } else {
            LOGGER.debug("Handling {} DataSet members for {}", members.size(), reportDescription);
        }
        for (final FcModelNode member : members) {
            if (member == null) {
                LOGGER.warn("Member == null in DataSet for {}", reportDescription);
                continue;
            }
            LOGGER.info("Handle member {} for {}", member.getReference(), reportDescription);
            try {
                this.addEventNotificationForReportedData(member, reportDescription);
            } catch (final Exception e) {
                LOGGER.error("Error adding event notification for member {} from {}", member.getReference(),
                        reportDescription, e);
            }
        }
    }

    private void addEventNotificationForReportedData(final FcModelNode evnRpn, final String reportDescription)
            throws ProtocolAdapterException {

        final EventType eventType = this.determineEventType(evnRpn, reportDescription);
        final Integer index = this.determineRelayIndex(evnRpn, reportDescription);
        final String description = this.determineDescription(evnRpn, reportDescription);

        this.deviceManagementService.addEventNotification(this.deviceIdentification, eventType.name(), description,
                index);
    }

    private EventType determineEventType(final FcModelNode evnRpn, final String reportDescription) {

        final String evnTypeName = "evnType";
        final BdaInt8U evnTypeNode = (BdaInt8U) evnRpn.getChild(evnTypeName);
        if (evnTypeNode == null) {
            throw this.childNodeNotAvailableException(evnRpn, evnTypeName, reportDescription);
        }

        final Short evnTypeCode = evnTypeNode.getValue();
        final EventType eventType = OSGP_EVENT_TYPE_PER_CODE.get(evnTypeCode);

        if (eventType == null) {
            final String exceptionMessage;
            if (OSGP_EVENT_TYPE_PER_CODE.containsKey(evnTypeCode)) {
                exceptionMessage = "No event mapping available for evnType: " + evnTypeCode + "("
                        + EVN_TYPE_DESCRIPTION_PER_CODE.get(evnTypeCode) + ") from " + reportDescription;
            } else {
                exceptionMessage = "Unknown evnType: " + evnTypeCode + " from " + reportDescription;
            }
            throw new IllegalArgumentException(exceptionMessage);
        }

        return eventType;
    }

    private Integer determineRelayIndex(final FcModelNode evnRpn, final String reportDescription) {

        final String swNumName = "swNum";
        final BdaInt8U swNumNode = (BdaInt8U) evnRpn.getChild(swNumName);
        if (swNumNode == null) {
            throw this.childNodeNotAvailableException(evnRpn, swNumName, reportDescription);
        }

        final Short swNum = swNumNode.getValue();

        return swNum.intValue();
    }

    private String determineDescription(final FcModelNode evnRpn, final String reportDescription) {

        final String remarkName = "remark";
        final BdaVisibleString remarkNode = (BdaVisibleString) evnRpn.getChild(remarkName);
        if (remarkNode == null) {
            throw this.childNodeNotAvailableException(evnRpn, remarkName, reportDescription);
        }

        return remarkNode.getStringValue();
    }

    private IllegalArgumentException childNodeNotAvailableException(final FcModelNode evnRpn,
            final String childNodeName,
            final String reportDescription) {
        return new IllegalArgumentException("No '" + childNodeName + "' child in DataSet member "
                + evnRpn.getReference() + " from " + reportDescription);
    }

    private void logReportDetails(final Report report) {
        final StringBuilder sb = new StringBuilder("Report details for device ").append(this.deviceIdentification)
                .append(System.lineSeparator());
        sb.append("\t             RptId:\t").append(report.getRptId()).append(System.lineSeparator());
        sb.append("\t        DataSetRef:\t").append(report.getDataSetRef()).append(System.lineSeparator());
        sb.append("\t           ConfRev:\t").append(report.getConfRev()).append(System.lineSeparator());
        sb.append("\t           BufOvfl:\t").append(report.isBufOvfl()).append(System.lineSeparator());
        sb.append("\t           EntryId:\t").append(report.getEntryId()).append(System.lineSeparator());
        sb.append("\tInclusionBitString:\t").append(Arrays.toString(report.getInclusionBitString()))
        .append(System.lineSeparator());
        sb.append("\tMoreSegmentsFollow:\t").append(report.isMoreSegmentsFollow()).append(System.lineSeparator());
        sb.append("\t             SqNum:\t").append(report.getSqNum()).append(System.lineSeparator());
        sb.append("\t          SubSqNum:\t").append(report.getSubSqNum()).append(System.lineSeparator());
        sb.append("\t       TimeOfEntry:\t").append(report.getTimeOfEntry()).append(System.lineSeparator());
        if (report.getTimeOfEntry() != null) {
            sb.append("\t                   \t(")
            .append(new DateTime(report.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET))
            .append(')').append(System.lineSeparator());
        }
        final List<BdaReasonForInclusion> reasonCodes = report.getReasonCodes();
        if (reasonCodes != null && !reasonCodes.isEmpty()) {
            sb.append("\t       ReasonCodes:").append(System.lineSeparator());
            for (final BdaReasonForInclusion reasonCode : reasonCodes) {
                sb.append("\t                   \t")
                .append(reasonCode.getReference() == null ? HexConverter.toHexString(reasonCode.getValue())
                        : reasonCode).append("\t(")
                        .append(this.reasonCodeInfo(reasonCode)).append(')').append(System.lineSeparator());
            }
        }
        sb.append("\t           optFlds:").append(report.getOptFlds()).append("\t(")
        .append(this.optFldsInfo(report.getOptFlds())).append(')').append(System.lineSeparator());
        final DataSet dataSet = report.getDataSet();
        if (dataSet == null) {
            sb.append("\t           DataSet:\tnull").append(System.lineSeparator());
        } else {
            sb.append("\t           DataSet:\t").append(dataSet.getReferenceStr()).append(System.lineSeparator());
            final List<FcModelNode> members = dataSet.getMembers();
            if (members != null && !members.isEmpty()) {
                sb.append("\t   DataSet members:\t").append(members.size()).append(System.lineSeparator());
                for (final FcModelNode member : members) {
                    sb.append("\t            member:\t").append(member).append(System.lineSeparator());
                    if (member.getReference().toString().contains("CSLC.EvnRpn")) {
                        sb.append(this.evnRpnInfo("\t                   \t\t", member));
                    }
                }
            }
        }
        LOGGER.info(sb.append(System.lineSeparator()).toString());
    }

    private String reasonCodeInfo(final BdaReasonForInclusion reason) {
        if (reason == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder();
        boolean addSeparator = false;
        if (reason.isApplicationTrigger()) {
            addSeparator = true;
            sb.append("ApplicationTrigger");
        }
        if (reason.isDataChange()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("DataChange");
        }
        if (reason.isDataUpdate()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("DataUpdate");
        }
        if (reason.isGeneralInterrogation()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("GeneralInterrogation");
        }
        if (reason.isIntegrity()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("Integrity");
        }
        if (reason.isQualityChange()) {
            if (addSeparator) {
                sb.append(", ");
            }
            sb.append("QualityChange");
        }
        return sb.toString();
    }

    private String optFldsInfo(final BdaOptFlds optFlds) {
        if (optFlds == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder();
        boolean addSeparator = false;
        if (optFlds.isBufferOverflow()) {
            addSeparator = true;
            sb.append("BufferOverflow");
        }
        if (optFlds.isConfigRevision()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("ConfigRevision");
        }
        if (optFlds.isDataReference()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("DataReference");
        }
        if (optFlds.isDataSetName()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("DataSetName");
        }
        if (optFlds.isEntryId()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("EntryId");
        }
        if (optFlds.isReasonForInclusion()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("ReasonForInclusion");
        }
        if (optFlds.isReportTimestamp()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("ReportTimestamp");
        }
        if (optFlds.isSegmentation()) {
            if (addSeparator) {
                sb.append(", ");
            } else {
                addSeparator = true;
            }
            sb.append("Segmentation");
        }
        if (optFlds.isSequenceNumber()) {
            if (addSeparator) {
                sb.append(", ");
            }
            sb.append("SequenceNumber");
        }
        return sb.toString();
    }

    private String evnRpnInfo(final String linePrefix, final FcModelNode evnRpn) {
        final StringBuilder sb = new StringBuilder();

        final BdaInt8U evnTypeNode = (BdaInt8U) evnRpn.getChild("evnType");
        sb.append(linePrefix).append("evnType: ");
        if (evnTypeNode == null) {
            sb.append("null");
        } else {
            final short evnType = evnTypeNode.getValue();
            sb.append(evnType).append(" = ").append(EVN_TYPE_DESCRIPTION_PER_CODE.get(evnType));
        }
        sb.append(System.lineSeparator());

        final BdaInt8U swNumNode = (BdaInt8U) evnRpn.getChild("swNum");
        sb.append(linePrefix).append("swNum: ");
        if (swNumNode == null) {
            sb.append("null");
        } else {
            final short swNum = swNumNode.getValue();
            sb.append(swNum).append(" = ").append("get external index for switch " + swNum);
        }
        sb.append(System.lineSeparator());

        final BdaInt8U trgTypeNode = (BdaInt8U) evnRpn.getChild("trgType");
        sb.append(linePrefix).append("trgType: ");
        if (trgTypeNode == null) {
            sb.append("null");
        } else {
            final short trgType = trgTypeNode.getValue();
            sb.append(trgType).append(" = ").append(TRG_TYPE_DESCRIPTION_PER_CODE.get(trgType));
        }
        sb.append(System.lineSeparator());

        final BdaBoolean swValNode = (BdaBoolean) evnRpn.getChild("swVal");
        sb.append(linePrefix).append("swVal: ");
        if (swValNode == null) {
            sb.append("null");
        } else {
            final boolean swVal = swValNode.getValue();
            sb.append(swVal).append(" = ").append(swVal ? "ON" : "OFF");
        }
        sb.append(System.lineSeparator());

        final BdaTimestamp trgTimeNode = (BdaTimestamp) evnRpn.getChild("trgTime");
        sb.append(linePrefix).append("trgTime: ");
        if (trgTimeNode == null || trgTimeNode.getDate() == null) {
            sb.append("null");
        } else {
            final DateTime trgTime = new DateTime(trgTimeNode.getDate());
            sb.append(trgTime);
        }
        sb.append(System.lineSeparator());

        final BdaVisibleString remarkNode = (BdaVisibleString) evnRpn.getChild("remark");
        sb.append(linePrefix).append("remark: ");
        if (remarkNode == null) {
            sb.append("null");
        } else {
            final String remark = remarkNode.getStringValue();
            sb.append(remark);
        }
        sb.append(System.lineSeparator());

        return sb.toString();
    }

    @Override
    public void associationClosed(final IOException e) {
        LOGGER.info("associationClosed for device: {}, {}", this.deviceIdentification, e == null ? "no IOException"
                : "IOException: " + e.getMessage());
    }
}
