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

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.HOURLY;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI3;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI4;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;

class StatusTest {
    private static final String TS_2022_01_12_18_00_00_S = "1642010400";
    private static final String TS_2022_01_12_18_10_00_S = "1642011000";
    private static final Long TS_2022_01_12_19_00_00 = 1642014000L;
    private static final Long TS_2022_01_12_20_21_40 = 1642018900L;

    TableStatus tableStatus = new TableStatus();
    List<String> expected;

    @BeforeEach
    void setUp() {
        expected = new ArrayList<>(List.of(KPI2, KPI1));
        final Map<String, KpiState> kpis = new HashMap<>();
        kpis.put(KPI1, new KpiState(true, TS_2022_01_12_18_00_00_S, TS_2022_01_12_18_00_00_S));
        kpis.put(KPI2, new KpiState(true, TS_2022_01_12_18_10_00_S, TS_2022_01_12_18_10_00_S));
        tableStatus.setAggregationPeriod(HOURLY);
        tableStatus.setListOfDimensions(List.of("agg_column_1", "agg_column_2"));
        tableStatus.setKpis(kpis);
    }

    @Test
    void whenGetColumnsCalled_shouldKpiNamesReturned() {
        assertThat(tableStatus.getExportableKpiList())
                .isEqualTo(expected);
    }

    @Test
    void whenGetColumnsCalled_shouldKpiNamesReturnedWithoutNonExportableKpi() {
        final Map<String, KpiState> kpis = new HashMap<>();
        kpis.put(KPI3, new KpiState(true, TS_2022_01_12_18_00_00_S, TS_2022_01_12_18_00_00_S));
        kpis.put(KPI4, new KpiState(false, TS_2022_01_12_18_10_00_S, TS_2022_01_12_18_10_00_S));
        tableStatus.setKpis(kpis);

        assertThat(tableStatus.getExportableKpiList())
                .doesNotContain(KPI4);
    }

    @Test
    void whenCheckForKpiChangeCalledWithoutChange_shouldNothingChange() {
        tableStatus.checkForKpiChange(List.of(KPI1, KPI2));

        assertThat(tableStatus.getExportableKpiList())
                .isEqualTo(expected);
    }

    @Test
    void whenCheckForKpiChangeCalledWithPlusKpi_shouldStatusContainTheNew() {
        tableStatus.checkForKpiChange(List.of(KPI1, KPI2, KPI3));

        assertThat(tableStatus.getKpis())
                .containsKey(KPI3);
    }

    @Test
    void whenCheckForKpiChangeCalledWithLessKpi_shouldStatusNotContainDeleted() {
        tableStatus.checkForKpiChange(List.of(KPI1));

        assertThat(tableStatus.getKpis().keySet())
                .doesNotContain(KPI2);
    }

    @Test
    void whenUpdateKpisCalled_shouldKpisContainTheNewData() {
        final ExecutionReportKpi firstExecutionReportKpi = buildDefaultExecutionReport().getTables().get(0).getExecutionReportKpis().get(0);
        final ExecutionReportKpi secondExecutionReportKpi = buildDefaultExecutionReport().getTables().get(0).getExecutionReportKpis().get(1);
        firstExecutionReportKpi.setReliabilityThreshold(TS_2022_01_12_19_00_00);
        secondExecutionReportKpi.setReliabilityThreshold(TS_2022_01_12_20_21_40);

        final List<ExecutionReportKpi> update = List.of(firstExecutionReportKpi, secondExecutionReportKpi);
        tableStatus.updateKpis(update);

        final String reliabilityFirst = tableStatus.getKpis().get(firstExecutionReportKpi.getName()).getReliabilityThreshold();
        final String reliabilitySecond = tableStatus.getKpis().get(secondExecutionReportKpi.getName()).getReliabilityThreshold();
        assertThat(firstExecutionReportKpi.getReliabilityThreshold())
                .hasToString(reliabilityFirst);
        assertThat(secondExecutionReportKpi.getReliabilityThreshold())
                .hasToString(reliabilitySecond);
    }

    @Test
    void whenKpiListNullOrExportableCalled_shouldReturnAllExceptNonExportableKpis() {
        tableStatus.getKpis().get(KPI1).setExportable(false);
        tableStatus.getKpis().put(KPI3, null);
        expected = List.of(KPI2, KPI3);
        assertThat(tableStatus.kpiListNullOrExportable())
                .isEqualTo(expected);
    }
}