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

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;

import lombok.NonNull;

/**
 * Component class for gathering metrics data about the PM Stats Exporter.
 */
@Component
public class MeterRegistryHelper {
    private final Set<String> existingTables = new HashSet<>();

    @Autowired
    private CustomMetrics customMetrics;

    /**
     * Helper method to increase the number of execution reports in metrics data.
     * @param scheduling Determines if the execution report is scheduled or on demand.
     */
    public void incrementExecutionReportCounter(@NonNull final ExecutionReport.Scheduling scheduling) {
        final boolean isScheduled = scheduling.equals(ExecutionReport.Scheduling.SCHEDULED);
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_execution_reports_from_kafka",
                "Number of scheduled/on-demand reports received from execution report topic",
                String.format("isScheduled=%s", isScheduled));
    }

    /**
     * Helper method to increase the metrics data regarding the execution report tables.
     * @param tableName The name of the table.
     * @param size The size of the table kpis.
     * @param scheduling Determines whether we want to gather metrics data
     *                   for the scheduled execution report tables or the on demand execution report tables.
     */
    public void increaseExecutionReportTablesCounter(final String tableName, final int size,
                                                     final ExecutionReport.Scheduling scheduling) {
        final boolean isScheduled = scheduling.equals(ExecutionReport.Scheduling.SCHEDULED);
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "occurrence_of_table_in_execution_reports",
                "Number of times a given table occurred in scheduled/on-demand reports from the execution reports topic",
                String.format("table=%s,isScheduled=%s", tableName, isScheduled));

        if (existingTables.add(tableName)) {
            customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_different_tables_in_execution_reports",
                    "Number of distinct tables received in scheduled/on-demand reports from the execution reports topic",
                    String.format("isScheduled=%s", isScheduled));
        }

        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tables_in_execution_reports",
                "Number of (non-distinct) tables received in reports from the execution reports topic",
                "");

        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_kpis_in_execution_reports",
                "Number of KPIs received from the execution reports topic",
                "",
                size);
    }

    /**
     * Helper method to increase the counter metrics data regarding the tables that were successfully exported.
     * @param tableName The name of the table.
     * @param topicName The topic name on which the metrics data will be increased.
     */
    public void increaseAvroExportedTablesCounter(final String tableName, final String topicName) {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "occurrence_of_a_table_in_an_avro_topic",
                "Number of times a given table export occurred in a given avro topic",
                String.format("table=%s,topic=%s", tableName, topicName));
    }

    /**
     * Helper method to increase the metrics data regarding the number of records put on the specified topic in Kafka.
     * @param topic The topic name on which the metrics data will be increased.
     */
    public void incrementRecordsPutOnKafka(final String topic) {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_records_put_on_kafka",
                "Number of records put on a given topic",
                String.format("topic=%s", topic));
    }

    /**
     * Helper method to increase the metrics data regarding the number of schemas created in data catalog.
     */
    public void incrementCreatedSchemasCounter() {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_created_schemas_in_data_catalog",
                "Number of schemas created in DataCatalog",
                "");
    }

    /**
     * Helper method to increment the metrics data regarding the number of failed execution report message validations.
     */
    public void incrementTransaction1FailedExecutionReportMsgValidationCounter() {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_1_failed_execution_report_msg_validation",
                "Number of failed execution report message validations in transaction_1",
                "");
    }

    /**
     * Helper method to increase the metrics data regarding the time of transaction 1 (processing execution report message).
     * @param time Amount of time in milliseconds the transaction took excluding the time spent waiting for dependent services (Kafka).
     */
    public void increaseTransaction1TimeCounter(final long time) {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "time_of_tr_1_ms",
                "Time of transaction_1 (processing execution report message) without waiting for other services (Kafka) in milliseconds",
                "",
                time);
    }

    /**
     * Helper method to increase the metrics data regarding the number of rows read from PostgresSQL, and the number of successful queries.
     * @param size The number of the rows read from PostgresSQL.
     */
    public void increaseTransaction2SuccessfulPostgresQueriesAndReadRowsCounters(final long size) {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_2_successful_postgres_queries",
                "Number of successful Postgres queries in transaction_2",
                "");

        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_read_rows_from_postgres",
                "Number of rows read from Postgres",
                "",
                size);
    }

    /**
     * Helper method to increment the metrics data regarding the number of empty Postgres queries.
     */
    public void incrementTransaction2EmptyPostgresQueriesCounter() {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_2_empty_postgres_queries",
                "Number of Postgres queries with empty results in transaction_2",
                "");
    }

    /**
     * Helper method to increment the metrics data regarding the number of successful Kafka writings in transaction 2.
     */
    public void incrementTransaction2SuccessfulKafkaWritingsCounter() {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_2_successful_kafka_writings",
                "Number of successful Kafka writings in transaction_2",
                "");
    }

    /**
     * Helper method to increment the metrics data regarding the number of processed completed-timestamp messages,
     * and the time of transaction 2 (processing completed-timestamp message).
     * @param time Amount of time in milliseconds the transaction took excluding the time spent waiting for
     *             dependent services (Kafka, SchemaRegistry, Postgres, DataCatalog).
     */
    public void incrementTransaction2ProcessedCompletedTimestampMsgAndTimeCounters(final long time) {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_2_processed_completed_timestamp_msg",
                "Number of processed completed-timestamp messages in transaction_2",
                "");

        customMetrics.incrementCounter(CustomMetrics.PREFIX + "time_of_tr_2_ms",
                "Time of transaction_2 (processing completed-timestamp message)"
                        + " without waiting for other services (Kafka, Postgres, SchemaRegistry, DataCatalog) in milliseconds",
                "",
                time);
    }

    /**
     * Helper method to increment the metrics data regarding the number of invalid completed-timestamp messages received.
     */
    public void incrementTransaction2InvalidCompletedTimestampMsgCounter() {
        customMetrics.incrementCounter(CustomMetrics.PREFIX + "number_of_tr_2_invalid_completed_timestamp_msg",
                "Number of completed-timestamp messages with invalid format in transaction_2",
                "");
    }

}
