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

package com.ericsson.oss.air.pm.stats.exporter.writer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.google.common.base.Stopwatch;

import lombok.NonNull;

/**
 * Component responsible for writing to Kafka in Avro or JSON format.
 * The writing is transactional, the component should notify the caller upon success/failure,
 * so the transaction could be handled in a proper way.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class KafkaWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaWriter.class);

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    private KafkaTemplate<String, GenericRecord> avroKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Object> jsonProducerTemplate;

    @Autowired
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

    /**
     * This method should be called when a table is ready to import, the data is fetched from the table and the Avro schema is ready.
     * If the transaction was a success, the method returns normally.
     * If any error occurred during the transaction, a RollBackException is thrown and the transaction is aborted.
     *
     * @param topic the topic where the data will be written to
     * @param rows  a list of GenericRecords which represents the data fetched from the table
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    public void sendAvro(@NonNull final String topic, final List<GenericRecord> rows) {
        final ArrayList<CompletableFuture<SendResult<String, GenericRecord>>> futures = new ArrayList<>(rows.size());
        LOGGER.info("Started writing messages to '{}' topic", topic);
        completedTimestampStopwatch.stop();
        for (final GenericRecord row : rows) {
            futures.add(avroKafkaTemplate.send(topic, row));
        }
        try {
            for (final Future<SendResult<String, GenericRecord>> future : futures) {
                future.get(1, TimeUnit.SECONDS);
                meterRegistryHelper.incrementRecordsPutOnKafka(topic);
            }
        } catch (final ExecutionException | TimeoutException e) {
            throw new RollBackException("Sending message to Kafka was not successful", e);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RollBackException("Thread was interrupted", e);
        }
        completedTimestampStopwatch.start();
        LOGGER.info("Finished writing messages to '{}' topic", topic);
    }

    /**
     * This method sends a Kafka message in JSON format to the specified topic
     *
     * This method is responsible for writing Status messages to the backup topic, and ColumnsAndTimeStamps messages,
     * to the completed timestamps topic.
     *
     * @param topic The name of the topic, where the message should be sent
     * @param key The key of the message
     * @param message either a Status, or a ColumnsAndTimeStamps object, the status of the inner state, or the timestamps to be exported
     */
    @SuppressWarnings({"PMD.PreserveStackTrace"})
    public void sendJson(@NonNull final String topic, @NonNull final String key, @NonNull final Object message) {
        LOGGER.info("Started writing message to '{}' topic", topic);
        LOGGER.debug("Key: {}, message: {}", key, message);
        try {
            jsonProducerTemplate.send(topic, key, message).get(1, TimeUnit.MINUTES);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RollBackException("Thread was interrupted", e);
        } catch (final ExecutionException | TimeoutException e) {
            throw new RollBackException("Sending message to Kafka was not successful", e);
        }
        LOGGER.info("Finished writing message to '{}' topic", topic);
    }
}
