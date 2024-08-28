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

package com.ericsson.oss.air.pm.stats.exporter.reader;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.processor.cts.CompletedTimestampsProcessor;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.registry.ExecutionReportProcessorRegistry;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.ExecutionReportValidatorUtil;
import com.ericsson.oss.air.pm.stats.exporter.utils.JsonLoggingUtils;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.ericsson.oss.air.pm.stats.exporter.utils.exception.InvalidJsonMsgException;
import com.google.common.base.Stopwatch;

import io.micrometer.core.annotation.Timed;

/**
 * Component responsible for receiving messages on the given kafka topic. The functionality of this class is only,
 * to read from a topic, and notify the necessary component, of the received message. Commit offset only, if the  message processing,
 * or the transaction was successful.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class KafkaReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReader.class);
    private static final String EXECUTION_REPORT_LISTENER_ID = "executionReportListenerId";
    private static final String COMPLETED_TIMESTAMP_LISTENER_ID = "completedTimestampListenerId";
    private static final String INNER_STATE_LISTENER_ID = "innerStateListener";

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopic;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTimestampTopic;

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    private Status statusOfTables;

    @Autowired
    @Qualifier("execution-report_stopwatch")
    private Stopwatch executionReportStopwatch;

    @Autowired
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

    @Autowired
    private ExecutionReportProcessorRegistry executionReportProcessorRegistry;

    @Autowired
    private CompletedTimestampsProcessor completedTimestampsProcessor;

    @Autowired
    private ExecutionReportValidatorUtil executionReportValidatorUtil;

    /**
     * Method automatically receiving deserialized messages from the given topic, cause of the @KafkaListener annotation.
     * Checks if the message is in the given format, and calls the inner state's processing method. If the call was
     * successful, the consumer offset will be committed, otherwise a warning log will be made.
     *
     * @param consumerRecord ConsumerRecord object containing the message
     */
    @Transactional("innerStateTransactionManager")
    @KafkaListener(
            id = EXECUTION_REPORT_LISTENER_ID,
            topics = "${kafka.topics.execution-report.topic-name}",
            groupId = "${kafka.topics.execution-report.group-id}",
            containerFactory = "executionReportListenerContainerFactory",
            autoStartup = "false",
            properties = {"spring.json.value.default.type=com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport"})
    @SuppressWarnings("PMD.PreserveStackTrace")
    @Timed(value = CustomMetrics.PREFIX + "execution_report_timer", description = "Time taken to process execution-report message")
    public void receiveOnExecutionReportTopic(final ConsumerRecord<?, ?> consumerRecord) {
        executionReportStopwatch.reset();
        executionReportStopwatch.start();
        try {
            final ExecutionReport executionReport = (ExecutionReport) consumerRecord.value();
            executionReportValidatorUtil.validateExecutionReportMessage(executionReport);
            executionReportProcessorRegistry.get(executionReport.getScheduled()).processMessage(executionReport);

            executionReportStopwatch.stop();
            meterRegistryHelper.increaseTransaction1TimeCounter(executionReportStopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (final InvalidJsonMsgException | ClassCastException e) {
            LOGGER.warn("Received invalid message on topic '{}': ", executionReportTopic, e);
            meterRegistryHelper.incrementTransaction1FailedExecutionReportMsgValidationCounter();

            executionReportStopwatch.stop();
        }
    }

    /**
     * Method automatically receiving deserialized messages from the given topic because of the @KafkaListener annotation.
     * Checks if the message is in the given format, then calls the processor for completed timestamp messages.
     * If the transaction was successful the consumer offset will be committed, otherwise an error log will be made.
     *
     * @param consumerRecord ConsumerRecord object containing the message
     */
    @Transactional(transactionManager = "completedTimestampTransactionManager")
    @KafkaListener(
            id = COMPLETED_TIMESTAMP_LISTENER_ID,
            topics = "${kafka.topics.completed-timestamp.topic-name}",
            groupId = "${kafka.topics.completed-timestamp.group-id}",
            containerFactory = "completedTimestampListenerContainerFactory",
            autoStartup = "false")
    @Timed(value = CustomMetrics.PREFIX + "completed_timestamp_timer", description = "Time taken to process completed-timestamp message")
    public void receiveOnCompletedTimestampTopic(final ConsumerRecord<?, ?> consumerRecord) throws SQLException {
        if (consumerRecord.value() instanceof ColumnsAndTimeStamps) {
            completedTimestampStopwatch.reset();
            completedTimestampStopwatch.start();

            final ColumnsAndTimeStamps columnsAndTimeStamps = (ColumnsAndTimeStamps) consumerRecord.value();

            final String logMessage = JsonLoggingUtils.objectToJson(columnsAndTimeStamps);
            LOGGER.debug("Received message, on topic {}: {}", completedTimestampTopic, logMessage);

            if (completedTimestampsProcessor.processCompletedTimestamps(columnsAndTimeStamps)) {
                completedTimestampStopwatch.stop();
                meterRegistryHelper.incrementTransaction2ProcessedCompletedTimestampMsgAndTimeCounters(
                        completedTimestampStopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        } else {
            LOGGER.warn("The received message on topic {} was not in the given format: {}", completedTimestampTopic, consumerRecord.value());
            meterRegistryHelper.incrementTransaction2InvalidCompletedTimestampMsgCounter();
        }
    }

    /**
     * Method automatically receiving inner state message from the given topic, cause of the @KafkaListener annotation,
     * and forwards it the inner state's restoring method.
     *
     * @param consumerRecord The latest inner state stored in Kafka
     */
    @KafkaListener(
            id = INNER_STATE_LISTENER_ID,
            topics = "${kafka.topics.backup.topic-name}",
            containerFactory = "backupListenerContainerFactory",
            autoStartup = "false")
    @Timed(value = CustomMetrics.PREFIX + "backup_timer", description = "Time taken to process backup topic message")
    public void readInnerStateFromKafka(final ConsumerRecord<?, ?> consumerRecord) {
        if (consumerRecord.value() instanceof Status) {
            LOGGER.debug("Received message, on topic {}, with key {}: {}", backupTopicName, consumerRecord.key(), consumerRecord.value());
            BeanUtils.copyProperties(consumerRecord.value(), statusOfTables);
        } else {
            LOGGER.warn("Received a message on topic {} not in the given format: {}", backupTopicName, consumerRecord.value());
        }
    }
}
