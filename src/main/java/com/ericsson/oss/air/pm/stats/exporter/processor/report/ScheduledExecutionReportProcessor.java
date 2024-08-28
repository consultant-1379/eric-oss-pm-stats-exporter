/*******************************************************************************
 * COPYRIGHT Ericsson 2024
 *
 *
 *
 * The copyright to the computer program(s) herein is the property of
 *
 * Ericsson Inc. The programs may be used and/or copied only with written
 *
 * permission from Ericsson Inc. or in accordance with the terms and
 *
 * conditions stipulated in the agreement/contract under which the
 *
 * program(s) have been supplied.
 ******************************************************************************/

package com.ericsson.oss.air.pm.stats.exporter.processor.report;

import static com.ericsson.oss.air.pm.stats.exporter.processor.report.helper.ReportProcessorHelper.labelsValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.api.ExecutionReportProcessor;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.helper.ReportProcessorHelper;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;

/**
 * Class responsible for processing scheduled execution report messages.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class ScheduledExecutionReportProcessor implements ExecutionReportProcessor {
    public static final Integer NO_AGGREGATION_PERIOD_VALUE = -1;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledExecutionReportProcessor.class);

    @Value("${meterCollectors.enabled}")
    private boolean collectorsEnabled;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    private CustomMetrics customMetrics;

    @Autowired
    private Status statusOfTables;

    @Autowired
    private ReportProcessorHelper reportProcessorHelper;

    @Override
    public boolean supports(final Scheduling scheduling) {
        return scheduling.equals(Scheduling.SCHEDULED);
    }

    @Override
    public void processMessage(final ExecutionReport message) {
        final long dttm = System.currentTimeMillis();
        final Map<String, String> lastExported = new HashMap<>();
        meterRegistryHelper.incrementExecutionReportCounter(message.getScheduled());

        for (final Table table : message.getTables()) {
            final String tableName = table.getName();
            final long lateDataStartTime = CalculationUtils.calculateStartTimeForLateData(table.getExecutionReportKpis());
            //Checks if the table already exists in the InnerState or not, if yes updates the data from it
            if (statusOfTables.containsTable(tableName)) {
                final TableStatus status = statusOfTables.get(tableName);
                status.setListOfDimensions(table.getListOfDimensions());
                status.checkForKpiChange(table.getListOfKpis());
                status.updateKpis(table.getExecutionReportKpis());
                LOGGER.info("The status has been updated for table: {}", tableName);
                final long exportedEndTime = checkForExport(status, tableName, lateDataStartTime, message.getExecutionId());
                if (exportedEndTime != 0) {
                    lastExported.put(tableName, String.valueOf(exportedEndTime));
                }

                /*if the table doesn't exist and the aggregation period is not -1 it will generate a new status for it and
                also makes a corresponding entry for it in the timestamps map
                 */
            } else if (!(table.getAggregationPeriod().equals(NO_AGGREGATION_PERIOD_VALUE))) {
                final TableStatus tableStatus = reportProcessorHelper.createNewTableStatus(table);
                statusOfTables.put(tableName, tableStatus);
                LOGGER.info("New tableStatus has been created for table: {}", tableName);
                final long exportedEndTime = checkForExport(tableStatus, tableName, lateDataStartTime, message.getExecutionId());
                if (exportedEndTime != 0) {
                    lastExported.put(tableName, String.valueOf(exportedEndTime));
                }
            }
            meterRegistryHelper.increaseExecutionReportTablesCounter(tableName,
                    table.getExecutionReportKpis().size(), message.getScheduled());

            if (collectorsEnabled) {
                final List<Pair<String, Long>> labelValueList = table.getExecutionReportKpis()
                        .stream()
                        .map(c -> labelsValue(message.getExecutionId(), tableName, c.getName(), message.getScheduled().name(),
                                c.isExportable(), dttm))
                        .collect(Collectors.toList());
                customMetrics.setGauges(
                        CustomMetrics.PREFIX + "exec_arrive",
                        "Collection of Execution Report arrival timestamps",
                        labelValueList);
            }
        }
        reportProcessorHelper.writeToBackupTopic(lastExported);
        statusOfTables.updateLastExported(lastExported);
    }

    /**
     * Calculates the reliability for the table, and if it's not 0, then calls the writeTimestamps() method.
     *
     * @param status    The status of a specific table
     * @param tableName The name of the table
     * @param lateDataStartTime The start time for late data
     * @param executionId id field of ExecutionReport
     * @return the calculated reliability
     */
    private long checkForExport(final TableStatus status, final String tableName, final long lateDataStartTime, final String executionId) {
        final long reliability = CalculationUtils.calculateSmallestReliability(status.getKpis().entrySet());
        if (reliability != 0) {
            final long startTime = Math.min(lateDataStartTime, Long.parseLong(status.getLastExported()));
            final ColumnsAndTimeStamps columnsAndTimeStamps =
                    new ColumnsAndTimeStamps(status.getListOfDimensions(), status.getExportableKpiList(), ExecutionReport.Scheduling.SCHEDULED,
                            String.valueOf(startTime), String.valueOf(reliability), tableName, executionId);
            reportProcessorHelper.writeTimestamps(columnsAndTimeStamps, status.getAggregationPeriod(), tableName);
        }
        return reliability;
    }
}
