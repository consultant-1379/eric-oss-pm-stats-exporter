/*******************************************************************************
 * COPYRIGHT Ericsson 2023
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

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_01_12_02_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling.ON_DEMAND;
import static com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling.SCHEDULED;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIMENSIONS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.HOURLY;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI_CELL_SECTOR_60;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.KpiState;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.helper.ReportProcessorHelper;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;

@SpringBootTest(classes = {ScheduledExecutionReportProcessor.class, Status.class})
@ActiveProfiles("kafkaTest")
@MockBeans({@MockBean(MeterRegistryHelper.class),
        @MockBean(CustomMetrics.class)})
class ScheduledExecutionReportProcessorTest {
    private static final String TS_2022_01_12_02_00_00_S = "1641952800";

    @Autowired
    private ScheduledExecutionReportProcessor scheduledExecutionReportProcessor;

    @Autowired
    private Status statusOfTables;

    @MockBean
    private ReportProcessorHelper reportProcessorHelper;

    @Test
    void whenSupportsCalledWithScheduled_shouldReturnTrue() {
        assertThat(scheduledExecutionReportProcessor.supports(SCHEDULED))
                .isTrue();
    }

    @Test
    void whenSupportsCalledWithOnDemand_shouldReturnFalse() {
        assertThat(scheduledExecutionReportProcessor.supports(ON_DEMAND))
                .isFalse();
    }

    @Test
    void whenNewTableReceived_shouldStatusContainKeyOfThatTableWithGoodValues() throws InterruptedException {
        doCallRealMethod().when(reportProcessorHelper).createNewTableStatus(any());

        BeanUtils.copyProperties(new Status(), statusOfTables);

        final ExecutionReport report = buildDefaultExecutionReport();
        report.getTables().get(0).getExecutionReportKpis().get(0).setReliabilityThreshold(TS_2022_01_12_02_00_00);
        report.getTables().get(0).getExecutionReportKpis().get(1).setReliabilityThreshold(TS_2022_01_12_02_00_00);

        scheduledExecutionReportProcessor.processMessage(report);

        assertThat(statusOfTables.containsTable(KPI_CELL_SECTOR_60))
                .isTrue();

        final TableStatus tableStatus = statusOfTables.get(KPI_CELL_SECTOR_60);
        final Table table = report.getTables().get(0);

        assertThat(table.getAggregationPeriod())
                .isEqualTo(tableStatus.getAggregationPeriod());
        assertThat(tableStatus.getListOfDimensions())
                .containsAll(table.getListOfDimensions());

        assertThat(table.getExecutionReportKpis().get(0).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI1).getReliabilityThreshold());
        assertThat(table.getExecutionReportKpis().get(1).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI2).getReliabilityThreshold());
    }

    @Test
    void whenExistingTableReceived_shouldStatusContainKeyOfThatTableWithGoodValues() throws InterruptedException {
        final Status status = new Status();
        final TableStatus localTableStatus = new TableStatus();
        localTableStatus.setAggregationPeriod(HOURLY);
        localTableStatus.setListOfDimensions(DIMENSIONS);
        localTableStatus.setKpis(new HashMap<>(Map.of(
                KPI1, new KpiState(true, null, TS_2022_01_12_02_00_00_S),
                KPI2, new KpiState(true, null, TS_2022_01_12_02_00_00_S))));
        localTableStatus.setLastExported(TS_2022_01_12_02_00_00_S);
        status.put(KPI_CELL_SECTOR_60, localTableStatus);
        BeanUtils.copyProperties(status, statusOfTables);

        scheduledExecutionReportProcessor.processMessage(buildDefaultExecutionReport());

        assertThat(statusOfTables.containsTable(KPI_CELL_SECTOR_60))
                .isTrue();

        final TableStatus tableStatus = statusOfTables.get(KPI_CELL_SECTOR_60);
        final Table table = buildDefaultExecutionReport().getTables().get(0);
        assertThat(table.getExecutionReportKpis().get(0).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI1).getReliabilityThreshold());
        assertThat(table.getExecutionReportKpis().get(1).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI2).getReliabilityThreshold());
    }
}