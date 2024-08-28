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

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

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
 * Processor for On demand Execution report.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class OnDemandExecutionReportProcessor implements ExecutionReportProcessor {
    @Value("${meterCollectors.enabled}")
    private boolean collectorsEnabled;

    @Autowired
    private ReportProcessorHelper reportProcessorHelper;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    private CustomMetrics customMetrics;

    @Override
    public boolean supports(final Scheduling scheduling) {
        return scheduling.equals(Scheduling.ON_DEMAND);
    }

    @Override
    public void processMessage(final ExecutionReport message) {
        final long dttm = System.currentTimeMillis();
        meterRegistryHelper.incrementExecutionReportCounter(message.getScheduled());

        for (final Table table : message.getTables()) {
            final String tableName = table.getName();
            final TableStatus tableStatus = reportProcessorHelper.createNewTableStatus(table);
            final long aggregation = tableStatus.getAggregationPeriod();
            final long startTime = CalculationUtils.calculateSmallestCalculationTime(tableStatus.getKpis().entrySet());
            final long endTime = CalculationUtils.calculateGreatestReliability(tableStatus.getKpis().entrySet());
            final ColumnsAndTimeStamps columnsAndTimeStamps =
                    new ColumnsAndTimeStamps(table.getListOfDimensions(), tableStatus.kpiListNullOrExportable(),
                            ExecutionReport.Scheduling.ON_DEMAND, String.valueOf(startTime), String.valueOf(endTime), table.getName(),
                            message.getExecutionId());

            reportProcessorHelper.writeTimestamps(columnsAndTimeStamps, aggregation, tableName);

            meterRegistryHelper.increaseExecutionReportTablesCounter(tableName,
                    table.getExecutionReportKpis().size(), message.getScheduled());

            if (collectorsEnabled) {
                final List<Pair<String, Long>> labelValueList = columnsAndTimeStamps.getKpis()
                        .stream()
                        .map(c -> labelsValue(message.getExecutionId(), tableName, c,
                                message.getScheduled().name(), true, dttm))
                        .collect(Collectors.toList());
                labelValueList.addAll(tableStatus.getKpis()
                        .keySet()
                        .stream()
                        .filter(key -> !columnsAndTimeStamps.getKpis().contains(key))
                        .map(c -> labelsValue(message.getExecutionId(), tableName, c, message.getScheduled().name(),
                                false, dttm))
                        .toList());
                customMetrics.setGauges(
                        CustomMetrics.PREFIX + "exec_arrive",
                        "Collection of Execution Report arrival timestamps",
                        labelValueList);
            }
        }
    }
}
