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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.utils;

import static com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils.calculateStartTimeForLateData;
import static com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils.calculateStartTimeForGenericKpi;
import static com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils.calculateSmallestReliability;
import static com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils.calculateSmallestCalculationTime;
import static com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils.calculateGreatestReliability;
import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.KpiState;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;

public class CalculationUtilsTest {
    private static final Long   TS_2022_05_05_01_00_00 = 1651712400L;
    private static final String TS_2022_05_05_01_00_00_S = "1651712400";
    private static final Long   TS_2022_05_05_02_00_00 = 1651716000L;
    private static final String TS_2022_05_05_02_00_00_S = "1651716000";
    private static final Long   TS_2022_05_05_03_00_00 = 1651719600L;
    private static final String TS_2022_05_05_03_00_00_S = "1651719600";
    private static final Long   TS_2022_05_05_04_00_00 = 1651723200L;
    private static final String TS_2022_05_05_04_00_00_S = "1651723200";
    private static final Long   TS_2022_05_05_05_00_00 = 1651726800L;
    private static final Long   TS_2022_05_05_06_00_00 = 1651730400L;
    private static final String KPI = "kpi";

    private static final List<ExecutionReportKpi> EXECUTION_REPORT_KPIS = List.of(
        new ExecutionReportKpi(KPI, true, TS_2022_05_05_03_00_00, true, TS_2022_05_05_03_00_00),
        new ExecutionReportKpi(KPI, true, TS_2022_05_05_04_00_00, true, TS_2022_05_05_04_00_00),
        new ExecutionReportKpi(KPI, true, TS_2022_05_05_02_00_00, false, TS_2022_05_05_02_00_00),
        new ExecutionReportKpi(KPI, true, TS_2022_05_05_05_00_00, false, TS_2022_05_05_05_00_00),
        new ExecutionReportKpi(KPI, false, TS_2022_05_05_01_00_00, false, TS_2022_05_05_01_00_00),
        new ExecutionReportKpi(KPI, false, TS_2022_05_05_06_00_00, false, TS_2022_05_05_06_00_00),
        new ExecutionReportKpi(KPI, false, TS_2022_05_05_02_00_00, true, TS_2022_05_05_02_00_00),
        new ExecutionReportKpi(KPI, false, TS_2022_05_05_05_00_00, true, TS_2022_05_05_05_00_00)
    );

    private static final Set<Entry<String, KpiState>> KPI_STATE_ENTRY_SET = EXECUTION_REPORT_KPIS
            .stream()
            .map(executionReportKpi -> Map.entry(executionReportKpi.getName(), KpiState.of(executionReportKpi)))
            .collect(Collectors.toSet());

    @Test
    void whenCalculateStartTimeForLateDataCalled_shouldCorrectValueBeReturned() {
        assertThat(calculateStartTimeForLateData(EXECUTION_REPORT_KPIS))
                .isEqualTo(TS_2022_05_05_03_00_00);
    }

    @Test
    void whenCalculateStartTimeForLateDataCalledWithoutExportableKpis_shouldMaxValueBeReturned() {
        assertThat(calculateStartTimeForLateData(List.of(
            new ExecutionReportKpi(KPI, false, TS_2022_05_05_01_00_00, false, TS_2022_05_05_01_00_00)
        ))).isEqualTo(MAX_VALUE);
    }

    @Test
    void whenCalculateStartTimeForGenericKpiCalledWithNonNullStartTime_shouldCorrectValueBeReturned() {
        assertThat(calculateStartTimeForGenericKpi(KPI_STATE_ENTRY_SET, 60))
                .isEqualTo(TS_2022_05_05_01_00_00_S);
    }

    @Test
    void whenCalculateStartTimeForGenericKpiCalledWithAllNullStartTime_shouldCorrectValueBeReturned() {
        assertThat(calculateStartTimeForGenericKpi(Set.of(
            Map.entry(KPI, new KpiState(true, null, TS_2022_05_05_04_00_00_S)),
            Map.entry(KPI, new KpiState(true, null, TS_2022_05_05_03_00_00_S))
        ), 60)).isEqualTo(TS_2022_05_05_02_00_00_S);
    }

    @Test
    void whenCalculateSmallestReliabilityCalledWithExportableKpis_shouldCorrectValueBeReturned() {
        assertThat(calculateSmallestReliability(KPI_STATE_ENTRY_SET))
                .isEqualTo(TS_2022_05_05_02_00_00);
    }

    @Test
    void whenCalculateSmallestReliabilityCalledWithOnlyNonExportableKpis_should0BeReturned() {
        assertThat(calculateSmallestReliability(Set.of(
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_04_00_00_S, TS_2022_05_05_04_00_00_S)),
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_03_00_00_S, TS_2022_05_05_03_00_00_S))
        ))).isZero();
    }

    @Test
    void whenCalculateSmallestReliabilityCalledWithAKpiThatIsNull_should0BeReturned() {
        // Map::entry, and Map::of throws NullPointerException for a null value,
        // but Map::put can handle it, hence this workaround to set the value to null
        final Map<String, KpiState> hashMapWithNull = new HashMap<>();
        hashMapWithNull.put(KPI, null);
        final Entry<String, KpiState> entry = hashMapWithNull.entrySet().stream().findFirst().get();

        assertThat(calculateSmallestReliability(Set.of(
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_04_00_00_S, TS_2022_05_05_04_00_00_S)),
            entry,
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_03_00_00_S, TS_2022_05_05_03_00_00_S))
        ))).isZero();
    }

    @Test
    void whenCalculateGreatestReliabilityCalledWithExportableKpis_shouldCorrectValueBeReturned() {
        assertThat(calculateGreatestReliability(KPI_STATE_ENTRY_SET))
                .isEqualTo(TS_2022_05_05_05_00_00);
    }

    @Test
    void whenCalculateGreatestReliabilityCalledWithOnlyNonExportableKpis_shouldMinValueBeReturned() {
        assertThat(calculateGreatestReliability(Set.of(
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_04_00_00_S, TS_2022_05_05_04_00_00_S)),
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_03_00_00_S, TS_2022_05_05_03_00_00_S))
        ))).isEqualTo(MIN_VALUE);
    }

    @Test
    void whenCalculateSmallestCalculationTimeCalledWithExportableKpis_shouldCorrectValueBeReturned() {
        assertThat(calculateSmallestCalculationTime(KPI_STATE_ENTRY_SET))
                .isEqualTo(TS_2022_05_05_02_00_00);
    }

    @Test
    void whenCalculateSmallestCalculationTimeCalledWithOnlyNonExportableKpis_shouldMaxValueBeReturned() {
        assertThat(calculateSmallestCalculationTime(Set.of(
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_04_00_00_S, TS_2022_05_05_04_00_00_S)),
            Map.entry(KPI, new KpiState(false, TS_2022_05_05_03_00_00_S, TS_2022_05_05_03_00_00_S))
        ))).isEqualTo(MAX_VALUE);
    }
}
