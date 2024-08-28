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

import static java.util.Map.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;

@SpringBootTest(classes = {MeterRegistryHelper.class})
@MockBeans({@MockBean(MeterRegistry.class)})
class MeterRegistryHelperTest {

    private static final String SCHEDULED_TOPIC = "pm-stats-calc-handling-avro-scheduled";
    private static final String ON_DEMAND_TOPIC = "pm-stats-calc-handling-avro-on-demand";
    private static final String TABLE_A = "kpi_tableA_60";
    private static final String TABLE_B = "kpi_tableB_60";
    private static final String TABLE_C = "kpi_tableC_60";
    private static final String NOT_IMPORTANT_TABLE = "kpi_not_important_table_60";
    private static final Scheduling SCHEDULED = Scheduling.SCHEDULED;
    private static final Scheduling ON_DEMAND = Scheduling.ON_DEMAND;
    private static final Integer NOT_IMPORTANT_NUMBER = 100;
    private static final String PREFIX = CustomMetrics.PREFIX;

    private static final Map<String, String> KEY_TO_METHOD_MAP = Map.ofEntries(
            entry(PREFIX + "number_of_created_schemas_in_data_catalog", "incrementCreatedSchemasCounter"),
            entry(PREFIX + "number_of_tr_1_failed_execution_report_msg_validation",
                    "incrementTransaction1FailedExecutionReportMsgValidationCounter"),
            entry(PREFIX + "number_of_tr_2_invalid_completed_timestamp_msg", "incrementTransaction2InvalidCompletedTimestampMsgCounter"),
            entry(PREFIX + "number_of_tr_2_empty_postgres_queries", "incrementTransaction2EmptyPostgresQueriesCounter"),
            entry(PREFIX + "number_of_tr_2_successful_kafka_writings", "incrementTransaction2SuccessfulKafkaWritingsCounter")
    );

    @MockBean
    private CustomMetrics customMetricsMock;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Test
    void whenIncrementExecutionReportCounterCalled_ShouldCounterIncrease() {
        meterRegistryHelper.incrementExecutionReportCounter(SCHEDULED);
        meterRegistryHelper.incrementExecutionReportCounter(ON_DEMAND);

        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_execution_reports_from_kafka"),
                eq("Number of scheduled/on-demand reports received from execution report topic"),
                eq("isScheduled=true"));
        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_execution_reports_from_kafka"),
                eq("Number of scheduled/on-demand reports received from execution report topic"),
                eq("isScheduled=false"));
    }

    @Test
    void whenIncreaseExecutionReportTablesCounterCalledWithAlreadyExistingTable_ShouldDifferentTableCounterNotIncrease() {
        meterRegistryHelper.increaseExecutionReportTablesCounter(NOT_IMPORTANT_TABLE, NOT_IMPORTANT_NUMBER, SCHEDULED);
        meterRegistryHelper.increaseExecutionReportTablesCounter(NOT_IMPORTANT_TABLE, NOT_IMPORTANT_NUMBER, SCHEDULED);

        verify(customMetricsMock, times(2)).incrementCounter(
                eq(PREFIX + "occurrence_of_table_in_execution_reports"),
                eq("Number of times a given table occurred in scheduled/on-demand reports from the execution reports topic"),
                eq(String.format("table=%s,isScheduled=%s", NOT_IMPORTANT_TABLE, "true")));
        verify(customMetricsMock, times(1)).incrementCounter(
                eq(PREFIX + "number_of_different_tables_in_execution_reports"),
                eq("Number of distinct tables received in scheduled/on-demand reports from the execution reports topic"),
                eq("isScheduled=true"));
        verify(customMetricsMock, times(2)).incrementCounter(
                eq(PREFIX + "number_of_tables_in_execution_reports"),
                eq("Number of (non-distinct) tables received in reports from the execution reports topic"),
                eq(""));
        verify(customMetricsMock, times(2)).incrementCounter(
                eq(PREFIX + "number_of_kpis_in_execution_reports"),
                eq("Number of KPIs received from the execution reports topic"),
                eq(""),
                eq(NOT_IMPORTANT_NUMBER.longValue()));
    }

    @Test
    void whenIncrementRecordsOfTableScheduledCalled_ShouldCounterIncrease() {
        meterRegistryHelper.incrementRecordsPutOnKafka(SCHEDULED_TOPIC);
        meterRegistryHelper.incrementRecordsPutOnKafka(ON_DEMAND_TOPIC);

        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_records_put_on_kafka"),
                eq("Number of records put on a given topic"),
                eq(String.format("topic=%s", SCHEDULED_TOPIC)));
        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_records_put_on_kafka"),
                eq("Number of records put on a given topic"),
                eq(String.format("topic=%s", ON_DEMAND_TOPIC)));
    }

    @ParameterizedTest(name = "[{index}] tableName: ''{0}'', scheduling: ''{1}'', isScheduled: ''{2}''")
    @MethodSource("staticParameterGenerator")
    void whenIncreaseExecutionReportTablesCounterCalled_ShouldCounterIncrease(final String tableName, final Scheduling scheduling,
                                                                              final String isScheduled) {
        meterRegistryHelper.increaseExecutionReportTablesCounter(tableName, NOT_IMPORTANT_NUMBER, scheduling);

        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "occurrence_of_table_in_execution_reports"),
                eq("Number of times a given table occurred in scheduled/on-demand reports from the execution reports topic"),
                eq(String.format("table=%s,isScheduled=%s", tableName, isScheduled)));
        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_different_tables_in_execution_reports"),
                eq("Number of distinct tables received in scheduled/on-demand reports from the execution reports topic"),
                eq(String.format("isScheduled=%s", isScheduled)));
        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_tables_in_execution_reports"),
                eq("Number of (non-distinct) tables received in reports from the execution reports topic"),
                eq(""));
        verify(customMetricsMock).incrementCounter(
                eq(PREFIX + "number_of_kpis_in_execution_reports"),
                eq("Number of KPIs received from the execution reports topic"),
                eq(""),
                eq(NOT_IMPORTANT_NUMBER.longValue()));
    }

    @ParameterizedTest(name = "[{index}] tableName: ''{0}'', scheduling: ''{1}''")
    @MethodSource("staticParameterGenerator")
    void whenIncreaseAvroExportedTablesCounterCalled_ShouldCounterIncrease(final String tableName, final Scheduling scheduling) {
        final String topic = scheduling == ExecutionReport.Scheduling.SCHEDULED ? SCHEDULED_TOPIC : ON_DEMAND_TOPIC;
        final String name = PREFIX + "occurrence_of_a_table_in_an_avro_topic";
        final String description = "Number of times a given table export occurred in a given avro topic";

        meterRegistryHelper.increaseAvroExportedTablesCounter(tableName, topic);

        verify(customMetricsMock).incrementCounter(
                eq(name),
                eq(description),
                eq(String.format("table=%s,topic=%s", tableName, topic)));
    }

    @ParameterizedTest(name = "[{index}] "
            + "microMeterName: ''{0}'', "
            + "methodNameToTest: ''{1}'', "
            + "paramTypes: ''{2}'', "
            + "params: ''{3}'', "
            + "mockCallCountEmpty: ''{4}'', "
            + "mockCallCountLong:''{5}''")
    @MethodSource("dynamicArgumentGenerator")
    @SneakyThrows
    void whenStaticKeysUsed_shouldNecessaryCountersIncrease(
            final String microMeterName,
            final String methodNameToTest,
            final Class[] paramTypes,
            final Object[] params,
            final Integer mockCallCountEmpty,
            final Integer mockCallCountLong
    ) {
        meterRegistryHelper.getClass().getDeclaredMethod(methodNameToTest, paramTypes).invoke(meterRegistryHelper, params);

        verify(customMetricsMock, times(mockCallCountEmpty)).incrementCounter(eq(microMeterName), any(), any());
        verify(customMetricsMock, times(mockCallCountLong)).incrementCounter(eq(microMeterName), any(), any(),
                eq(NOT_IMPORTANT_NUMBER.longValue()));
    }

    private static Stream<Arguments> staticParameterGenerator() {
        return Stream.of(
            Arguments.of(TABLE_A, SCHEDULED, "true"),
            Arguments.of(TABLE_B, ON_DEMAND, "false"),
            Arguments.of(TABLE_C, ON_DEMAND, "false")
        );
    }

    private static Stream<Arguments> dynamicArgumentGenerator() {
        return Stream.concat(
            Stream.of(
                Arguments.of(PREFIX + "time_of_tr_1_ms",
                        "increaseTransaction1TimeCounter",
                        new Class[]{long.class}, new Object[]{NOT_IMPORTANT_NUMBER}, 0, 1),
                Arguments.of(PREFIX + "number_of_tr_2_successful_postgres_queries",
                        "increaseTransaction2SuccessfulPostgresQueriesAndReadRowsCounters",
                        new Class[]{long.class}, new Object[]{NOT_IMPORTANT_NUMBER}, 1, 0),
                Arguments.of(PREFIX + "number_of_read_rows_from_postgres",
                        "increaseTransaction2SuccessfulPostgresQueriesAndReadRowsCounters",
                        new Class[]{long.class}, new Object[]{NOT_IMPORTANT_NUMBER}, 0, 1),
                Arguments.of(PREFIX + "number_of_tr_2_processed_completed_timestamp_msg",
                        "incrementTransaction2ProcessedCompletedTimestampMsgAndTimeCounters",
                        new Class[]{long.class}, new Object[]{NOT_IMPORTANT_NUMBER}, 1, 0),
                Arguments.of(PREFIX + "time_of_tr_2_ms",
                        "incrementTransaction2ProcessedCompletedTimestampMsgAndTimeCounters",
                        new Class[]{long.class}, new Object[]{NOT_IMPORTANT_NUMBER}, 0, 1)
            ),
            KEY_TO_METHOD_MAP.entrySet().stream().map(entry -> Arguments.of(entry.getKey(), entry.getValue(), new Class[]{}, new Object[]{}, 1, 0))
        );
    }
}