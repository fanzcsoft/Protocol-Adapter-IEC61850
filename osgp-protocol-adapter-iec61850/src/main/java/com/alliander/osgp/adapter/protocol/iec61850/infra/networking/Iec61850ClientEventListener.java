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

public class Iec61850ClientEventListener implements ClientEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850ClientEventListener.class);

    private static final Map<Short, String> EVN_TYPE_DESCRIPTION_PER_CODE = new TreeMap<>();
    private static final Map<Short, String> TRG_TYPE_DESCRIPTION_PER_CODE = new TreeMap<>();

    static {
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 1, "DIAG_EVENTS_GENERAL");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 2, "LIGHT_EVENTS_LIGHT_ON");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 3, "LIGHT_EVENTS_LIGHT_OFF");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 4, "TARIFF_EVENTS_TARIFF_ON");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 5, "TARIFF_EVENTS_TARIFF_OFF");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 6, "MONITOR_EVENTS_LOSS_OF_POWER");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 7, "FUNCTION_FIRMWARE_EVENTS_ACTIVATING");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 8, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 9, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_FAILED");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 10, "FUNCTION_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 11, "SECURITY_FIRMWARE_EVENTS_ACTIVATING");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 12, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 13, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_FAILED");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 14, "SECURITY_FIRMWARE_EVENTS_DOWNLOAD_SUCCESS");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 15, "CA_FILE_EVENTS_ACTIVATING");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 16, "CA_FILE_FIRMWARE_EVENTS_DOWNLOAD_NOTFOUND");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 17, "CA_FILE_EVENTS_DOWNLOAD_FAILED");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 18, "CA_FILE_EVENTS_DOWNLOAD_SUCCESS");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 19, "NTP_SERVER_NOT_REACH");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 20, "NTP_SYNC_ALARM_OFFSET");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 21, "NTP_SYNC_MAX_OFFSET");
        EVN_TYPE_DESCRIPTION_PER_CODE.put((short) 22, "AUTHENTICATION_FAIL");

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
        LOGGER.info("newReport for device: {}, reportId: {}", this.deviceIdentification, report.getRptId());
        this.logReportDetails(report);
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
            sb.append("\t                   \t(").append(new DateTime(report.getTimeOfEntry().getTimestampValue()))
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
