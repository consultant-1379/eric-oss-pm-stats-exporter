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

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.ON_DEMAND;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.helper.ReportProcessorHelper;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;

@SpringBootTest(classes = OnDemandExecutionReportProcessor.class)
@ActiveProfiles("kafkaTest")
@MockBeans({@MockBean(MeterRegistryHelper.class),
        @MockBean(CustomMetrics.class)})
public class OnDemandExecutionReportProcessorTest {
    @MockBean
    private ReportProcessorHelper reportProcessorHelperMock;

    @Autowired
    private OnDemandExecutionReportProcessor onDemandExecutionReportProcessor;

    @ParameterizedTest(name = "[{index}] scheduled: ''{0}''")
    @EnumSource(Scheduling.class)
    void whenSupportsCalled_shouldCorrectValueBeReturned(final Scheduling scheduling) {
        assertThat(onDemandExecutionReportProcessor.supports(scheduling))
                .isEqualTo(scheduling.equals(Scheduling.ON_DEMAND));
    }

    @Test
    void whenOnDemandFlowCalled_shouldWriterTimestampsBeCalledWithCorrectValues() {
        final ExecutionReport executionReport = buildDefaultExecutionReport();
        executionReport.setScheduled(ON_DEMAND);
        executionReport.getTables().get(0).getExecutionReportKpis().get(0).setCalculationStartTime(TS_2022_05_17_12_00_00);
        executionReport.getTables().get(0).getExecutionReportKpis().get(1).setCalculationStartTime(TS_2022_05_17_12_00_00);
        executionReport.getTables().get(0).getExecutionReportKpis().get(0).setReliabilityThreshold(TS_2022_05_17_13_00_00);
        executionReport.getTables().get(0).getExecutionReportKpis().get(1).setReliabilityThreshold(TS_2022_05_17_13_00_00);
        doReturn(new TableStatus(executionReport.getTables().get(0))).when(reportProcessorHelperMock).createNewTableStatus(any(Table.class));

        onDemandExecutionReportProcessor.processMessage(executionReport);

        final Table tableExpected = executionReport.getTables().get(0);
        final ColumnsAndTimeStamps columnsAndTimeStampsExpected =
                new ColumnsAndTimeStamps(tableExpected.getListOfDimensions(), tableExpected.getListOfKpis(), ON_DEMAND,
                    TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, tableExpected.getName(), EXECUTION_ID);

        final ArgumentCaptor<Table> tableArgumentCaptor = ArgumentCaptor.forClass(Table.class);
        final ArgumentCaptor<ColumnsAndTimeStamps> columnsAndTimeStampsArgumentCaptor = ArgumentCaptor.forClass(ColumnsAndTimeStamps.class);
        final ArgumentCaptor<Long> aggregationPeriodArgumentCaptor = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> tableNameArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(reportProcessorHelperMock).createNewTableStatus(tableArgumentCaptor.capture());
        verify(reportProcessorHelperMock).writeTimestamps(
                columnsAndTimeStampsArgumentCaptor.capture(), aggregationPeriodArgumentCaptor.capture(), tableNameArgumentCaptor.capture());

        final Table tableActual = tableArgumentCaptor.getValue();
        assertThat(tableActual.getName())
                .isEqualTo(tableExpected.getName());
        assertThat(tableActual.getAggregationPeriod())
                .isEqualTo(tableExpected.getAggregationPeriod());
        assertThat(tableActual.getExecutionReportKpis())
                .containsExactlyInAnyOrderElementsOf(tableExpected.getExecutionReportKpis());
        assertThat(tableActual.getListOfDimensions())
                .containsExactlyInAnyOrderElementsOf(tableExpected.getListOfDimensions());
        assertThat(tableActual.getListOfKpis())
                .containsExactlyInAnyOrderElementsOf(tableExpected.getListOfKpis());

        final ColumnsAndTimeStamps columnsAndTimeStampsActual = columnsAndTimeStampsArgumentCaptor.getValue();
        assertThat(columnsAndTimeStampsActual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(columnsAndTimeStampsExpected);

        assertThat(aggregationPeriodArgumentCaptor.getValue())
                .isEqualTo(60);
        assertThat(tableNameArgumentCaptor.getValue())
                .isEqualTo(tableExpected.getName());
    }
}
