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

package com.ericsson.oss.air.pm.stats.exporter.model.innerstate;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_01_12_02_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_01_12_04_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThat;


import org.junit.jupiter.api.Test;

public class KpiStateTest {
    @Test
    void whenExecutionReportCreated_shouldKpiInterfaceMethodsReturnCorrectValues() {
        final KpiState actualKpiState = KpiState.of(buildDefaultExecutionReport()
                .getTables()
                .get(0)
                .getExecutionReportKpis()
                .get(0));

        final KpiState expectedKpiState = new KpiState(
                true,
                TS_2022_01_12_02_00_00_S,
                TS_2022_01_12_04_00_00_S
        );

        assertThat(actualKpiState)
                .satisfies(kpiState -> assertThat(kpiState.longCalculationStartTime()).isEqualTo(expectedKpiState.longCalculationStartTime()))
                .satisfies(kpiState -> assertThat(kpiState.longReliabilityThreshold()).isEqualTo(expectedKpiState.longReliabilityThreshold()))
                .satisfies(kpiState -> assertThat(kpiState.isExportable()).isEqualTo(expectedKpiState.isExportable()))
                .satisfies(kpiState -> assertThat(kpiState.isReexportLateDataEnabled()).isFalse())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedKpiState);
    }
}
