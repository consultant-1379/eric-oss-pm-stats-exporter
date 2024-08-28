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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.helper;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.data.util.Pair;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.config.sr.SchemaRegistryConfig;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.writer.KafkaWriter;
import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;


/**
 * Component class containing shared methods for execution report processors.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class ReportProcessorHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportProcessorHelper.class);
    private static final Integer EXIT_CODE = 42;
    private static final String INNER_STATE_PRODUCER_KEY = "eric-oss-pm-stats-exporter";

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTsTopicName;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private KafkaWriter writer;

    @Autowired
    private SchemaRegistryConfig schemaRegistry;

    @Autowired
    private RetryTemplate schemaRegistryRetryTemplate;

    @Autowired
    private Status statusOfTables;

    @Autowired
    @Qualifier("execution-report_stopwatch")
    private Stopwatch executionReportStopwatch;

    /**
     * Shared method to create new TableStatus for an execution report table.
     * @param table The name of the table
     * @return the new TableStatus
     */
    public TableStatus createNewTableStatus(final Table table) {
        final TableStatus tableStatus = new TableStatus(table);
        setSubjectCompatibility(table.getName());
        return tableStatus;
    }

    /**
     * Sets a compatibility update for an execution report table.
     * @param tableName The name of the table
     */
    @SneakyThrows
    public void setSubjectCompatibility(final String tableName) {
        schemaRegistryRetryTemplate.execute(arg -> schemaRegistry.updateCompatibilityFull(tableName),
                context -> {
                    LOGGER.error("Schema Registry is unavailable and connection wasn't restored in time");
                    SpringApplication.exit(applicationContext, () -> EXIT_CODE);
                    return null;
                });
    }

    /**
     * Shared method to write start and end timestamps.
     * @param columnsAndTimeStamps representing the TimeStamps information of an execution report table.
     * @param aggregationPeriod aggregation period
     * @param tableName The name of the table
     */
    public void writeTimestamps(final ColumnsAndTimeStamps columnsAndTimeStamps, final long aggregationPeriod, final String tableName) {
        long startTimeToWrite = Long.parseLong(columnsAndTimeStamps.getStartTime());
        final long endTime = Long.parseLong(columnsAndTimeStamps.getEndTime());
        long endTimeToWrite = startTimeToWrite + aggregationPeriod * 60;
        while (endTimeToWrite <= endTime) {
            columnsAndTimeStamps.setEndTime(String.valueOf(endTimeToWrite));
            columnsAndTimeStamps.setStartTime(String.valueOf(startTimeToWrite));
            executionReportStopwatch.stop();
            writer.sendJson(completedTsTopicName, tableName, columnsAndTimeStamps);
            executionReportStopwatch.start();
            startTimeToWrite = endTimeToWrite;
            endTimeToWrite = startTimeToWrite + aggregationPeriod * 60;
            LOGGER.info("New ColumnsAndTimestamps saved successfully on the {} topic.", completedTsTopicName);
        }
    }

    /**
     * Writes the status of tables attribute to the backup topic.
     *
     * @param exportedTs A Map containing the new exported end times for the tables, that were exported.
     */
    public void writeToBackupTopic(final Map<String, String> exportedTs) {
        final Status statusToWrite = new Status();
        BeanUtils.copyProperties(statusOfTables, statusToWrite);
        statusToWrite.updateLastExported(exportedTs);
        writer.sendJson(backupTopicName, INNER_STATE_PRODUCER_KEY, statusToWrite);
    }

    /**
     * Creates a Pair containing labels and value belonging to the exec_arrive custom metric.
     * @param executionId The value of the label key "id"
     * @param tableName The value of the label key "table"
     * @param kpi The value of the label key "kpi"
     * @param scheduled The value of the label key "scheduled"
     * @param exportable The value of the label key "exportable"
     * @param value The current value of the exec_arrive custom metric
     * @return the Pair containing labels and value created from the parameters
     */
    public static Pair<String, Long> labelsValue(final String executionId, final String tableName, final String kpi,
                                          final String scheduled, final boolean exportable, final long value) {
        return Pair.of(String.format("exportable=%s,id=%s,kpi=%s,scheduled=%s,table=%s,",
                exportable, executionId, kpi, scheduled, tableName), value);
    }
}