package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.openmuc.openiec61850.BdaOptFlds;
import org.openmuc.openiec61850.BdaReasonForInclusion;
import org.openmuc.openiec61850.DataSet;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.HexConverter;
import org.openmuc.openiec61850.Report;

import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;

public class Iec61850ClientRTUEventListener extends Iec61850ClientBaseEventListener {

    /**
     * The EntryTime from IEC61850 has timestamp values relative to 01-01-1984.
     * TimeStamp values and Java date time values have milliseconds since
     * 01-01-1970. The milliseconds between these representations are in the
     * following offset.
     */
    private static final long IEC61850_ENTRY_TIME_OFFSET = 441763200000L;

    /*
     * Node names of EvnRpn nodes that occur as members of the report dataset.
     */

    public Iec61850ClientRTUEventListener(final String deviceIdentification) throws ProtocolAdapterException {
        super(deviceIdentification, Iec61850ClientRTUEventListener.class);
    }

    @Override
    public void newReport(final Report report) {
        final DateTime timeOfEntry = report.getTimeOfEntry() == null ? null
                : new DateTime(report.getTimeOfEntry().getTimestampValue() + IEC61850_ENTRY_TIME_OFFSET);

        final String reportDescription = String.format("device: %s, reportId: %s, timeOfEntry: %s, sqNum: %s%s%s",
                this.deviceIdentification, report.getRptId(), timeOfEntry == null ? "-" : timeOfEntry,
                report.getSqNum(), report.getSubSqNum() == null ? "" : " subSqNum: " + report.getSubSqNum(),
                report.isMoreSegmentsFollow() ? " (more segments follow for this sqNum)" : "");
        this.logger.info("newReport for {}", reportDescription);
        boolean skipRecordBecauseOfOldSqNum = false;
        if (report.isBufOvfl()) {
            this.logger.warn("Buffer Overflow reported for {} - entries within the buffer may have been lost.",
                    reportDescription);
        } else if (this.firstNewSqNum != null && report.getSqNum() != null) {
            if (report.getSqNum() < this.firstNewSqNum) {
                skipRecordBecauseOfOldSqNum = true;
            }
        }
        this.logReportDetails(report);

        final DataSet dataSet = report.getDataSet();
        if (dataSet == null) {
            this.logger.warn("No DataSet available for {}", reportDescription);
            return;
        }
        final List<FcModelNode> members = dataSet.getMembers();
        if (members == null || members.isEmpty()) {
            this.logger.warn("No members in DataSet available for {}", reportDescription);
            return;
        } else {
            this.logger.debug("Handling {} DataSet members for {}", members.size(), reportDescription);
        }
        for (final FcModelNode member : members) {
            if (member == null) {
                this.logger.warn("Member == null in DataSet for {}", reportDescription);
                continue;
            }
            this.logger.info("Handle member {} for {}", member.getReference(), reportDescription);
            try {
                if (skipRecordBecauseOfOldSqNum) {
                    this.logger.warn(
                            "Skipping report because SqNum: {} is less than what should be the first new value: {}",
                            report.getSqNum(), this.firstNewSqNum);
                } else {
                    // TODO handle dataset member
                    // this.addEventNotificationForReportedData(member,
                    // timeOfEntry, reportDescription);
                }
            } catch (final Exception e) {
                this.logger.error("Error adding event notification for member {} from {}", member.getReference(),
                        reportDescription, e);
            }
        }
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
                                : reasonCode)
                        .append("\t(").append(this.reasonCodeInfo(reasonCode)).append(')')
                        .append(System.lineSeparator());
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
                    sb.append("\t                   \t\t").append(member);
                }
            }
        }
        this.logger.info(sb.append(System.lineSeparator()).toString());
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

    // private String evnRpnInfo(final String linePrefix, final FcModelNode
    // evnRpn) {
    // final StringBuilder sb = new StringBuilder();
    //
    // final BdaInt8U evnTypeNode = (BdaInt8U)
    // evnRpn.getChild(EVENT_NODE_EVENT_TYPE);
    // sb.append(linePrefix).append(EVENT_NODE_EVENT_TYPE).append(": ");
    // if (evnTypeNode == null) {
    // sb.append("null");
    // } else {
    // final short evnType = evnTypeNode.getValue();
    // sb.append(evnType).append(" =
    // ").append(EventType.forCode(evnType).getDescription());
    // }
    // sb.append(System.lineSeparator());
    //
    // final BdaInt8U swNumNode = (BdaInt8U)
    // evnRpn.getChild(EVENT_NODE_SWITCH_NUMBER);
    // sb.append(linePrefix).append(EVENT_NODE_SWITCH_NUMBER).append(": ");
    // if (swNumNode == null) {
    // sb.append("null");
    // } else {
    // final short swNum = swNumNode.getValue();
    // sb.append(swNum).append(" = ").append("get external index for switch " +
    // swNum);
    // }
    // sb.append(System.lineSeparator());
    //
    // final BdaInt8U trgTypeNode = (BdaInt8U)
    // evnRpn.getChild(EVENT_NODE_TRIGGER_TYPE);
    // sb.append(linePrefix).append(EVENT_NODE_TRIGGER_TYPE).append(": ");
    // if (trgTypeNode == null) {
    // sb.append("null");
    // } else {
    // final short trgType = trgTypeNode.getValue();
    // sb.append(trgType).append(" =
    // ").append(TRG_TYPE_DESCRIPTION_PER_CODE.get(trgType));
    // }
    // sb.append(System.lineSeparator());
    //
    // final BdaBoolean swValNode = (BdaBoolean)
    // evnRpn.getChild(EVENT_NODE_SWITCH_VALUE);
    // sb.append(linePrefix).append(EVENT_NODE_SWITCH_VALUE).append(": ");
    // if (swValNode == null) {
    // sb.append("null");
    // } else {
    // final boolean swVal = swValNode.getValue();
    // sb.append(swVal).append(" = ").append(swVal ? "ON" : "OFF");
    // }
    // sb.append(System.lineSeparator());
    //
    // final BdaTimestamp trgTimeNode = (BdaTimestamp)
    // evnRpn.getChild(EVENT_NODE_TRIGGER_TIME);
    // sb.append(linePrefix).append(EVENT_NODE_TRIGGER_TIME).append(": ");
    // if (trgTimeNode == null || trgTimeNode.getDate() == null) {
    // sb.append("null");
    // } else {
    // final DateTime trgTime = new DateTime(trgTimeNode.getDate());
    // sb.append(trgTime);
    // }
    // sb.append(System.lineSeparator());
    //
    // final BdaVisibleString remarkNode = (BdaVisibleString)
    // evnRpn.getChild(EVENT_NODE_REMARK);
    // sb.append(linePrefix).append(EVENT_NODE_REMARK).append(": ");
    // if (remarkNode == null) {
    // sb.append("null");
    // } else {
    // final String remark = remarkNode.getStringValue();
    // sb.append(remark);
    // }
    // sb.append(System.lineSeparator());
    //
    // return sb.toString();
    // }

    @Override
    public void associationClosed(final IOException e) {
        this.logger.info("associationClosed for device: {}, {}", this.deviceIdentification,
                e == null ? "no IOException" : "IOException: " + e.getMessage());

        // TODO store data in response queue for notifications
        /**
         * synchronized (this.eventNotifications) { if
         * (this.eventNotifications.isEmpty()) { logger.info(
         * "No event notifications received from device: {}",
         * this.deviceIdentification); return; }
         * Collections.sort(this.eventNotifications, NOTIFICATIONS_BY_TIME); try
         * { this.deviceManagementService.addEventNotifications(this.
         * deviceIdentification, this.eventNotifications); } catch (final
         * ProtocolAdapterException pae) { logger.error(
         * "Error adding device notifications for device: " +
         * this.deviceIdentification, pae); } }
         */
    }
}
