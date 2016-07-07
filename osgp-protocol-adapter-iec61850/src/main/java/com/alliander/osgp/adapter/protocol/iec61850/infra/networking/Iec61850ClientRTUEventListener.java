package com.alliander.osgp.adapter.protocol.iec61850.infra.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.openmuc.openiec61850.BdaOptFlds;
import org.openmuc.openiec61850.BdaReasonForInclusion;
import org.openmuc.openiec61850.DataSet;
import org.openmuc.openiec61850.FcModelNode;
import org.openmuc.openiec61850.HexConverter;
import org.openmuc.openiec61850.Report;

import com.alliander.osgp.adapter.protocol.iec61850.application.services.DeviceManagementService;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ProtocolAdapterException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DataAttribute;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalNode;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.NodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.ReadOnlyNodeContainer;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.SubDataAttribute;
import com.alliander.osgp.dto.valueobjects.microgrids.DataResponseDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementDto;
import com.alliander.osgp.dto.valueobjects.microgrids.MeasurementResultSystemIdentifierDto;

public class Iec61850ClientRTUEventListener extends Iec61850ClientBaseEventListener {

    /**
     * The EntryTime from IEC61850 has timestamp values relative to 01-01-1984.
     * TimeStamp values and Java date time values have milliseconds since
     * 01-01-1970. The milliseconds between these representations are in the
     * following offset.
     */
    private static final long IEC61850_ENTRY_TIME_OFFSET = 441763200000L;

    public Iec61850ClientRTUEventListener(final String deviceIdentification,
            final DeviceManagementService deviceManagementService) throws ProtocolAdapterException {
        super(deviceIdentification, deviceManagementService, Iec61850ClientRTUEventListener.class);
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

        if (skipRecordBecauseOfOldSqNum) {
            this.logger.warn("Skipping report because SqNum: {} is less than what should be the first new value: {}",
                    report.getSqNum(), this.firstNewSqNum);
            return;
        }

        this.logReportDetails(report);
        try {
            this.processDataSet(report.getDataSet(), reportDescription);
        } catch (final ProtocolAdapterException e) {
            this.logger.warn("Unable to process report, discarding report", e);
        }
    }

    private void processDataSet(final DataSet dataSet, final String reportDescription) throws ProtocolAdapterException {
        if (dataSet == null) {
            this.logger.warn("No DataSet available for {}", reportDescription);
            return;
        }

        final List<FcModelNode> members = dataSet.getMembers();
        if (members == null || members.isEmpty()) {
            this.logger.warn("No members in DataSet available for {}", reportDescription);
            return;
        }

        final List<MeasurementDto> measurements = new ArrayList<>();
        for (final FcModelNode member : members) {
            if (member == null) {
                this.logger.warn("Member == null in DataSet for {}", reportDescription);
                continue;
            }

            this.logger.info("Handle member {} for {}", member.getReference(), reportDescription);
            try {
                final MeasurementDto dto = this
                        .translatePvMeasurement(new ReadOnlyNodeContainer(this.deviceIdentification, member));
                if (dto != null) {
                    measurements.add(dto);
                } else {
                    this.logger.warn("Unsupprted member {}, skipping", member.getName());
                }
            } catch (final Exception e) {
                this.logger.error("Error adding event notification for member {} from {}", member.getReference(),
                        reportDescription, e);
            }
        }

        final MeasurementResultSystemIdentifierDto systemMeasurements = new MeasurementResultSystemIdentifierDto(1,
                LogicalNode.GENERATOR_ONE.getDescription(), measurements);
        final List<MeasurementResultSystemIdentifierDto> systems = new ArrayList<>();
        systems.add(systemMeasurements);
        this.deviceManagementService.sendMeasurements(this.deviceIdentification, new DataResponseDto(systems));
    }

    private MeasurementDto translatePvMeasurement(final ReadOnlyNodeContainer node) {
        if (node.getFcmodelNode().getName().equals(DataAttribute.BEHAVIOR.getDescription())) {
            return this.translatePvBehavior(node);
        }

        if (node.getFcmodelNode().getName().equals(DataAttribute.GENERATOR_SPEED.getDescription())) {
            return this.translatePvGenerationSpeed(node);
        }

        if (node.getFcmodelNode().getName().equals(DataAttribute.DEMANDED_POWER.getDescription())) {
            // TODO add suupport for demand power
            return null;
        }

        if (node.getFcmodelNode().getName().equals(DataAttribute.HEALTH.getDescription())) {
            return this.translatePvHealth(node);
        }

        if (node.getFcmodelNode().getName().equals(DataAttribute.OPERATIONAL_HOURS.getDescription())) {
            return this.translatePvOperationalHours(node);
        }

        return null;
    }

    private MeasurementDto translatePvBehavior(final ReadOnlyNodeContainer node) {
        return new MeasurementDto(1, DataAttribute.BEHAVIOR.getDescription(), 0,
                new DateTime(node.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                node.getByte(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto translatePvGenerationSpeed(final ReadOnlyNodeContainer node) {
        final NodeContainer generationMagnitude = node.getChild(SubDataAttribute.MAGNITUDE);
        return new MeasurementDto(1, DataAttribute.GENERATOR_SPEED.getDescription(), 0,
                new DateTime(node.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                generationMagnitude.getFloat(SubDataAttribute.FLOAT).getFloat());
    }

    private MeasurementDto translatePvOperationalHours(final ReadOnlyNodeContainer node) {
        return new MeasurementDto(1, DataAttribute.OPERATIONAL_HOURS.getDescription(), 0,
                new DateTime(node.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                node.getInteger(SubDataAttribute.STATE).getValue());
    }

    private MeasurementDto translatePvHealth(final ReadOnlyNodeContainer node) {
        return new MeasurementDto(1, DataAttribute.HEALTH.getDescription(), 0,
                new DateTime(node.getDate(SubDataAttribute.TIME), DateTimeZone.UTC),
                node.getByte(SubDataAttribute.STATE).getValue());
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

    @Override
    public void associationClosed(final IOException e) {
        this.logger.info("associationClosed for device: {}, {}", this.deviceIdentification,
                e == null ? "no IOException" : "IOException: " + e.getMessage());
    }
}
