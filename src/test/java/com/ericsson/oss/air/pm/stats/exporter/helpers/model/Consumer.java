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

package com.ericsson.oss.air.pm.stats.exporter.helpers.model;

import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.lang.NonNull;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;

/**
 * Class to be used by the KafkaMessageListenerContainer. This class is responsible for handling the
 * received messages.
 */
public class Consumer implements MessageListener<String, Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);

    private final String groupId;
    private final SynchronousQueue<Object> messages = new SynchronousQueue<>();

    public Consumer() {
        groupId = "null";
    }

    public Consumer(final String groupId) {
        this.groupId = groupId;
    }

    public Object getNext(final long timeout, final TimeUnit timeUnit) throws InterruptedException {
        return messages.poll(timeout, timeUnit);
    }

    /**
     * This method calls flush(int, TimeUnit), with the default value of 2 seconds.
     *
     * @throws InterruptedException While the thread waits for a message to arrive it could be
     *                              interrupted
     */
    public void flush() throws InterruptedException {
        flush(2, TimeUnit.SECONDS);
    }

    /**
     * This method reads all the messages received by the consumer, and ignores them, hence clean
     * the consumer to get in a clean state.
     *
     * @param timeout  The amount of maximum time each read should wait
     * @param timeUnit The timeunit corresponding to the timeout
     * @throws InterruptedException While the thread waits for a message to arrive it could be
     *                              interrupted
     */
    public void flush(final int timeout, final TimeUnit timeUnit) throws InterruptedException {
        Object message = messages.poll(timeout, timeUnit);
        while (message != null) {
            LOGGER.info("Flushed message in consumer, with group-id {}: {}", groupId, message);
            message = messages.poll(timeout, timeUnit);
        }
    }

    /**
     * This method is invoked automatically by Spring, when a new message is received on the given
     * topic.
     *
     * @param message The message received on the topic.
     */
    @Override
    public void onMessage(@NonNull final ConsumerRecord<String, Object> message) {
        LOGGER.info("RECEIVED message from topic {}, with group-id {}: {}", message.topic(), groupId, message.value().toString());
        try {
            if (message.value() instanceof String) {
                messages.put(message.value());
            } else if (message.value() instanceof ColumnsAndTimeStamps) {
                messages.put((ColumnsAndTimeStamps) message.value());
            } else if (message.value() instanceof Status) {
                messages.put((Status) message.value());
            } else if (message.value() instanceof GenericRecord) {
                messages.put((GenericRecord) message.value());
            } else {
                final Map<String, Object> map = (Map<String, Object>) message.value();
                messages.put(map);
            }
        } catch (final Exception e) {
            LOGGER.error("Casting was not successful", e);
        }
    }
}
