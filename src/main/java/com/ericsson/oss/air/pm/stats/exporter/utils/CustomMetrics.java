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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.MultiGauge;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Component class for gathering custom metrics data about the PM Stats Exporter.
 */
@Component
public class CustomMetrics {
    public static final String PREFIX = "pm_stats_exporter_";

    @Autowired
    @Setter
    private MeterRegistry meterRegistry;

    @Getter
    private final List<GaugeData> gaugeDataList = new ArrayList<>();

    private final Map<String, Counter> counterMap = new ConcurrentHashMap<>();
    private final Map<String, MultiGauge> multiGaugeMap = new HashMap<>();
    private long lastHelperListRemoveEpochTime = System.currentTimeMillis();

    @Value("${meterCollectors.retentionSeconds}")
    private long retentionSeconds;

    Iterable<Tag> getTagListFromString(final String labels) {
        if (labels.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(labels.split(",")).map(l -> {
            final String[] keyValue = l.split("=");
            return Tag.of(keyValue[0], keyValue[1]);
        }).collect(Collectors.toList());
    }

    private Tags getTagsFromString(final String labels) {
        final Iterable<Tag> tags = getTagListFromString(labels);
        return Tags.empty().and(tags);
    }

    /**
     * Initialize Counter metrics.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @PostConstruct
    private void initializeCounters() {
        initializeCounter(CustomMetrics.PREFIX + "number_of_execution_reports_from_kafka",
                "Number of scheduled/on-demand reports received from execution report topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_created_schemas_in_data_catalog",
                "Number of schemas created in DataCatalog",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_different_tables_in_execution_reports",
                "Number of distinct tables received in scheduled/on-demand reports from the execution reports topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_kpis_in_execution_reports",
                "Number of KPIs received from the execution reports topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_read_rows_from_postgres",
                "Number of rows read from Postgres",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tables_in_execution_reports",
                "Number of (non-distinct) tables received in reports from the execution reports topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_1_failed_execution_report_msg_validation",
                "Number of failed execution report message validations in transaction_1",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_2_empty_postgres_queries",
                "Number of Postgres queries with empty results in transaction_2",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_2_invalid_completed_timestamp_msg",
                "Number of completed-timestamp messages with invalid format in transaction_2",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_2_processed_completed_timestamp_msg",
                "Number of processed completed-timestamp messages in transaction_2",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_2_successful_kafka_writings",
                "Number of successful Kafka writings in transaction_2",
                "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_tr_2_successful_postgres_queries",
                "Number of successful Postgres queries in transaction_2",
                "");
        initializeCounter(CustomMetrics.PREFIX + "occurrence_of_table_in_execution_reports",
                "Number of times a given table occurred in scheduled/on-demand reports from the execution reports topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "occurrence_of_a_table_in_an_avro_topic",
                "Number of times a given table export occurred in a given avro topic",
                "");
        initializeCounter(CustomMetrics.PREFIX + "time_of_tr_1_ms",
                "Time of transaction_1 (processing execution report message) without waiting for other services (Kafka) in milliseconds",
                "");
        initializeCounter(CustomMetrics.PREFIX + "time_of_tr_2_ms",
                "Time of transaction_2 (processing completed-timestamp message)"
                        + " without waiting for other services (Kafka, Postgres, SchemaRegistry, DataCatalog) in milliseconds", "");
        initializeCounter(CustomMetrics.PREFIX + "number_of_records_put_on_kafka",
                "Number of records put on a given topic",
                "");
    }

    /**
     * Initialize Timer metrics.
     */
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @PostConstruct
    private void initializeTimers() {
        final String kafkaReaderTimerTags = "class=com.ericsson.oss.air.pm.stats.exporter.reader.KafkaReader,"
                + "exception=none,method=readInnerStateFromKafka";
        final String executionReportValidatorUtilTags = "class="
                + "com.ericsson.oss.air.pm.stats.exporter.utils.ExecutionReportValidatorUtil,"
                + "exception=none,method=validateExecutionReportMessage";

        initializeTimer(CustomMetrics.PREFIX + "execution_report_timer",
                "Time taken to process execution-report message",
                kafkaReaderTimerTags);
        initializeTimer(CustomMetrics.PREFIX + "completed_timestamp_timer",
                "Time taken to process completed-timestamp message",
                kafkaReaderTimerTags);
        initializeTimer(CustomMetrics.PREFIX + "backup_timer",
                "Time taken to process backup topic message",
                kafkaReaderTimerTags);
        initializeTimer(CustomMetrics.PREFIX + "execution_report_validation_timer",
                "Time taken to validate execution-report message",
                executionReportValidatorUtilTags);
    }

    /**
     * Initializes a Counter metric with the given name, description and label, if not created yet.
     *
     * @param name name of the metric
     * @param description description of the metric
     * @param labels labels of the metric
     * @return returns the initialized counter
     */
    public Counter initializeCounter(final String name, final String description, final String labels) {
        final String key = String.join("|", name, labels);
        if (!counterMap.containsKey(key)) {
            counterMap.computeIfAbsent(key, s -> {
                final Counter counter;
                if (labels.isEmpty() || labels.isBlank()) {
                    counter = Counter.builder(name)
                            .description(description)
                            .register(meterRegistry);
                } else {
                    final String keyNoLabel = String.join("|", name, "");
                    if (counterMap.containsKey(keyNoLabel)) {
                        // We need to remove metric without label, otherwise Prometheus does not show the same metric with label
                        final Counter counterWithoutLabel = counterMap.get(keyNoLabel);
                        meterRegistry.remove(counterWithoutLabel);
                        counterMap.remove(keyNoLabel);
                    }
                    counter = Counter.builder(name)
                            .description(description)
                            .tags(getTagListFromString(labels))
                            .register(meterRegistry);
                }
                return counter;
            });
        }
        return counterMap.get(key);
    }

    private void initializeTimer(final String name, final String description, final String tags) {
        Timer.builder(name)
                .description(description)
                .tags(getTagListFromString(tags))
                .register(meterRegistry);
    }

    /**
     * Creates a Counter metric with the given name, description and label if not created yet, and increases its value
     * by 1.
     *
     * @param name name of the metric
     * @param description description of the metric
     * @param labels labels of the metric
     */
    public void incrementCounter(final String name, final String description, final String labels) {
        incrementCounter(name, description, labels, 1L);
    }

    /**
     * Creates a Counter metric with the given name, description and label if not created yet, and increases its value
     * by the given value.
     *
     * @param name name of the metric
     * @param description description of the metric
     * @param labels labels of the metric
     * @param incValue value to be added to the current value of the metric
     */
    public void incrementCounter(final String name, final String description, final String labels, final long incValue) {
        final Counter counter = initializeCounter(name, description, labels);
        counter.increment(incValue);
    }

    /**
     * Creates a Gauge metric with the given name, description and label if not created yet, and sets its value.
     *
     * @param name name of the metric
     * @param description description of the metric
     * @param labelValueList list of label-value pairs of the metric
     */
    public void setGauges(final String name, final String description, final List<Pair<String, Long>> labelValueList) {
        synchronized (this) {
            final long currentEpochTime = System.currentTimeMillis();
            gaugeDataList.addAll(labelValueList.stream()
                    .map(labelValue -> new GaugeData(name, labelValue.getFirst(), labelValue.getSecond(), currentEpochTime))
                    .toList());
            if (currentEpochTime - lastHelperListRemoveEpochTime > retentionSeconds * 1000L) {
                final List<GaugeData> gaugeDataToRemoveList = gaugeDataList.stream()
                        .filter(m -> currentEpochTime - m.epochTime > retentionSeconds * 1000L)
                        .peek(m -> meterRegistry.remove(meterRegistry.get(m.name).tags(getTagListFromString(m.labels)).gauge()))
                        .toList();
                gaugeDataList.removeIf(gaugeDataToRemoveList::contains);
                lastHelperListRemoveEpochTime = currentEpochTime;
            }
            final MultiGauge multiGauge = multiGaugeMap.computeIfAbsent(name, s -> MultiGauge.builder(name)
                    .description(description)
                    .register(meterRegistry));
            multiGauge.register(gaugeDataList.stream()
                    .filter(gaugeData -> gaugeData.name.equals(name))
                    .map(gaugeData -> MultiGauge.Row.of(getTagsFromString(gaugeData.labels), gaugeData.value))
                    .collect(Collectors.toList()), true);
        }
    }

    @AllArgsConstructor
    static class GaugeData {
        @Getter
        private String name;
        @Getter
        private String labels;
        @Getter
        private long value;

        private long epochTime;
    }
}
