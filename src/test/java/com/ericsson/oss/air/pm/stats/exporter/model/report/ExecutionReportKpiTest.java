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

package com.ericsson.oss.air.pm.stats.exporter.model.report;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_01_12_02_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_01_12_04_00_00;
import static org.assertj.core.api.Assertions.assertThat;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;

import org.junit.jupiter.api.Test;

public class ExecutionReportKpiTest {
    @Test
    void whenExecutionReportCreated_shouldKpiInterfaceMethodsReturnCorrectValues() {
        final ExecutionReportKpi actualExecutionReportKpi = buildDefaultExecutionReport().getTables().get(0).getExecutionReportKpis().get(0);
        final ExecutionReportKpi expectedExecutionReportKpi = new ExecutionReportKpi(
                KPI1,
                false,
                TS_2022_01_12_02_00_00,
                true,
                TS_2022_01_12_04_00_00
        );

        assertThat(actualExecutionReportKpi)
                .satisfies(kpiState -> assertThat(kpiState.longCalculationStartTime())
                    .isEqualTo(expectedExecutionReportKpi.longCalculationStartTime()))
                .satisfies(kpiState -> assertThat(kpiState.longReliabilityThreshold())
                    .isEqualTo(expectedExecutionReportKpi.longReliabilityThreshold()))
                .satisfies(kpiState -> assertThat(kpiState.isExportable()).isEqualTo(expectedExecutionReportKpi.isExportable()))
                .satisfies(kpiState -> assertThat(kpiState.isReexportLateDataEnabled())
                    .isEqualTo(expectedExecutionReportKpi.isReexportLateDataEnabled()))
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedExecutionReportKpi);
    }
}
