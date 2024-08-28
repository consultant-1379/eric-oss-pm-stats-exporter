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

import static com.ericsson.oss.air.pm.stats.exporter.processor.report.ScheduledExecutionReportProcessor.NO_AGGREGATION_PERIOD_VALUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.utils.exception.InvalidJsonMsgException;

import io.micrometer.core.annotation.Timed;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import lombok.NonNull;


/**
 * Utility class for validating the incoming execution report from PM Stats Calculator.
 */
@Component
public class ExecutionReportValidatorUtil {
    private static final List<Integer> VALID_AGGREGATION_PERIODS = new ArrayList<>(List.of(NO_AGGREGATION_PERIOD_VALUE, 15, 60, 1440));

    /**
     * helper method to validate the execution report.
     * @param executionReportKpi The kpi of the table to be validated.
     * @param table helper parameter to get the proper information for the exception message.
     * */
    private void checkKpiProperties(final ExecutionReportKpi executionReportKpi, final Table table) throws InvalidJsonMsgException {
        final String kpiName = executionReportKpi.getName();
        final List<String> listOfKpis = table.getListOfKpis();
        final String tableName = table.getName();
        if (!(listOfKpis.contains(kpiName))) {
            throw new InvalidJsonMsgException("In table %s the kpi %s is not in list_of_kpis %s.%n", tableName, kpiName, listOfKpis);
        }
        final Long reliabilityThreshold = executionReportKpi.getReliabilityThreshold();

        if (executionReportKpi.isReexportLateDataEnabled()) {
            final Long calculationStartTime = executionReportKpi.getCalculationStartTime();
            if (calculationStartTime > reliabilityThreshold) {
                throw new InvalidJsonMsgException(
                        "In table %s in kpi %s calculation_start_time %s is greater than reliability_threshold %s.%n",
                        tableName, kpiName, calculationStartTime, reliabilityThreshold);
            }
        }
    }

    /**
     * Validates incoming execution report message.
     * @param report The execution report to be validated.
     * */
    @Timed(value = CustomMetrics.PREFIX + "execution_report_validation_timer", description = "Time taken to validate execution-report message")
    public void validateExecutionReportMessage(@NonNull final ExecutionReport report) throws InvalidJsonMsgException {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            final Set<ConstraintViolation<ExecutionReport>> violations = validatorFactory.getValidator().validate(report);
            if (!(violations.isEmpty())) {
                throw new InvalidJsonMsgException(violations.iterator().next().getMessage());
            }
        }
        final Long executionStartTime = report.getExecutionStart();
        final Long executionEndTime = report.getExecutionEnd();
        if (executionStartTime > executionEndTime) {
            throw new InvalidJsonMsgException("The execution_start_time %s is greater than execution_end_time %s.%n",
                    executionStartTime, executionEndTime);
        }
        for (final Table table : report.getTables()) {
            final String tableName = table.getName();
            final Integer aggregationPeriod = table.getAggregationPeriod();
            if (!(VALID_AGGREGATION_PERIODS.contains(aggregationPeriod))) {
                throw new InvalidJsonMsgException("The aggregation_period (%d) of table %s is invalid.%n",
                        aggregationPeriod, tableName);
            }
            if (CollectionUtils.containsAny(table.getListOfDimensions(), table.getListOfKpis())) {
                throw new InvalidJsonMsgException(
                        "In table %s lists of dimensions %s and lists of kpis %s contain common elements.%n",
                        tableName, table.getListOfDimensions(), table.getListOfKpis());
            }
            for (final ExecutionReportKpi executionReportKpi : table.getExecutionReportKpis()) {
                checkKpiProperties(executionReportKpi, table);
            }
        }
    }
}