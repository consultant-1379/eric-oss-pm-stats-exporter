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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.AGGREGATION_BEGIN_TIME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.AGGREGATION_END_TIME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.utils.exception.InvalidJsonMsgException;

@SpringBootTest(classes = {ExecutionReportValidatorUtil.class})
@ActiveProfiles("test")
public class ExecutionReportValidatorTest {
    private static final Long   TS_1970_01_01_00_59_59 = 3599L;
    private static final Long   TS_2999_12_31_23_00_00 = 32503676400L;

    final ExecutionReport report = new ExecutionReport();

    @Autowired
    private ExecutionReportValidatorUtil executionReportValidatorUtil;

    @BeforeEach
    void init() {
        buildDefaultExecutionReport(report);
    }

    @Test
    void whenExecutionReportIsValid() {
        assertThatCode(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .doesNotThrowAnyException();
    }

    @Test
    void whenExecutionReportIsNull() {
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void whenExecutionIdIsNullOrEmptyOrInvalid() {
        report.setExecutionId("badExecutionID");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionId(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionId("");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenScheduledFieldIsNull() {
        report.setScheduled(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenExecutionEndTimeIsNullOrEmptyOrInvalidOrNotInRange() {
        report.setExecutionEnd(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionEnd(TS_1970_01_01_00_59_59);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionEnd(TS_2999_12_31_23_00_00);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenExecutionStartTimeIsNullOrEmptyOrInvalidOrNotInRange() {
        report.setExecutionStart(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionStart(TS_1970_01_01_00_59_59);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionStart(TS_2999_12_31_23_00_00);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenExecutionGroupNameIsEmptyOrNull() {
        report.setExecutionGroup(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setExecutionGroup("");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenTablesFieldIsEmptyOrNullOrContainsDuplicatedElementsOrOneTableIsNull() {
        report.getTables().add(report.getTables().get(0));
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.getTables().set(0, null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setTables(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        report.setTables(new ArrayList<>());
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenTableNameIsNullOrEmptyOrInvalid() {
        final Table table0 = report.getTables().get(0);
        table0.setName(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setName("");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setName("Invalid name");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenAggregationPeriodIsNullOrInvalid() {
        final Table table0 = report.getTables().get(0);
        table0.setAggregationPeriod(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setAggregationPeriod(123);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenListOfDimensionsIsEmptyOrNullOrContainsDuplicatedElementsOrOneDimensionIsNullOrEmpty() {
        final Table table0 = report.getTables().get(0);
        table0.getListOfDimensions().add(table0.getListOfDimensions().get(0));
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.getListOfDimensions().set(0, null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.getListOfDimensions().set(0, "");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setListOfDimensions(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setListOfDimensions(new ArrayList<>());
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenListOfKpisIsEmptyOrNullOrContainsDuplicatedElementsOrOneKpiIsNullOrEmpty() {
        final Table table0 = report.getTables().get(0);
        table0.getListOfKpis().add(table0.getListOfKpis().get(0));
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.getListOfKpis().set(0, null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.getListOfKpis().set(0, "");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setListOfKpis(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setListOfKpis(new ArrayList<>());
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenKpisIsEmptyOrNullOrContainsDuplicatedElementsOrOneKpiIsNull() {
        final Table table0 = report.getTables().get(0);
        table0.getExecutionReportKpis().add(table0.getExecutionReportKpis().get(0));
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.getExecutionReportKpis().set(0, null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setExecutionReportKpis(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        table0.setExecutionReportKpis(new ArrayList<>());
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenKpiNameIsNullOrEmpty() {
        final ExecutionReportKpi executionReportKpi0 = report.getTables().get(0).getExecutionReportKpis().get(0);
        executionReportKpi0.setName(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        executionReportKpi0.setName("");
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenCalculationStartTimeIsNullOrEmptyOrNotInRange() {
        final ExecutionReportKpi executionReportKpi0 = report.getTables().get(0).getExecutionReportKpis().get(0);
        executionReportKpi0.setReexportLateDataEnabled(true);
        executionReportKpi0.setCalculationStartTime(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        executionReportKpi0.setCalculationStartTime(TS_1970_01_01_00_59_59);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        executionReportKpi0.setCalculationStartTime(TS_2999_12_31_23_00_00);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenReliabilityThresholdIsNullOrEmptyOrNotInRange() {
        final ExecutionReportKpi executionReportKpi0 = report.getTables().get(0).getExecutionReportKpis().get(0);
        executionReportKpi0.setReliabilityThreshold(null);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        executionReportKpi0.setReliabilityThreshold(TS_1970_01_01_00_59_59);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
        executionReportKpi0.setReliabilityThreshold(TS_2999_12_31_23_00_00);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenExecutionStartTimeIsGreaterThanExecutionEndTime() {
        report.setExecutionStart(report.getExecutionEnd() + 1);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenCalculationStartTimeIsGreaterThanReliabilityThreshold() {
        final Table table = report.getTables().get(0);
        final ExecutionReportKpi executionReportKpi = table.getExecutionReportKpis().get(1);
        executionReportKpi.setReexportLateDataEnabled(true);
        executionReportKpi.setCalculationStartTime(executionReportKpi.getReliabilityThreshold() + 1);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenListOfDimensionsAndListOfKpisHaveCommonElement() {
        report.getTables().get(0).setListOfDimensions(List.of(DIM1, KPI1, AGGREGATION_BEGIN_TIME, AGGREGATION_END_TIME));
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }

    @Test
    void whenKpiNameIsNotInListOfKpis() {
        final String missingKpiName = "kpi_invalid";
        report.getTables().get(0).getExecutionReportKpis().get(0).setName(missingKpiName);
        assertThatThrownBy(() -> executionReportValidatorUtil.validateExecutionReportMessage(report))
                .isInstanceOf(InvalidJsonMsgException.class);
    }
}
