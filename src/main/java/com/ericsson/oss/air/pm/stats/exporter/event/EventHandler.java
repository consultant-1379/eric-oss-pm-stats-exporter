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

package com.ericsson.oss.air.pm.stats.exporter.event;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.exception.InnerStateException;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Class responsible for handling events.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);
    private static final String LISTENER_IS_NULL_ERROR = "Container Listener returned with null for listener id: ";
    private static final String INNER_STATE_LISTENER_ID = "innerStateListener";
    private static final String EXECUTION_REPORT_LISTENER_ID = "executionReportListenerId";

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopicName;

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    /**
     * Upon gaining leadership starts the backup topic listener for 10 seconds, then starts the execution report topic listener.
     * @param event Received OnGrantedEvent.
     */
    @EventListener(OnGrantedEvent.class)
    public void onGrantedEventHandler(final OnGrantedEvent event) {
        LOGGER.info("Kubernetes leadership granted for this pod: {}", event);

        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(INNER_STATE_LISTENER_ID)).orElseThrow(() ->
                new InnerStateException(LISTENER_IS_NULL_ERROR + INNER_STATE_LISTENER_ID)).start();

        Uninterruptibles.sleepUninterruptibly(10, TimeUnit.SECONDS);

        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(INNER_STATE_LISTENER_ID)).orElseThrow(() ->
                new InnerStateException(LISTENER_IS_NULL_ERROR + INNER_STATE_LISTENER_ID)).stop();
        LOGGER.info("Reading from '{}' topic stopped.", backupTopicName);

        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(EXECUTION_REPORT_LISTENER_ID)).orElseThrow(() ->
                new InnerStateException(LISTENER_IS_NULL_ERROR + EXECUTION_REPORT_LISTENER_ID)).start();
        LOGGER.info("Reading from '{}' topic started.", executionReportTopicName);
    }

    /**
     * Upon leadership getting revoked stops the execution report topic listener.
     * @param event Received OnRevokedEvent.
     */
    @EventListener(OnRevokedEvent.class)
    public void onRevokedEventHandler(final OnRevokedEvent event) {
        LOGGER.info("Kubernetes leadership revoked from this pod: {}", event);

        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(EXECUTION_REPORT_LISTENER_ID)).orElseThrow(() ->
                new InnerStateException(LISTENER_IS_NULL_ERROR + EXECUTION_REPORT_LISTENER_ID)).stop();
        LOGGER.info("Reading from '{}' topic stopped.", executionReportTopicName);
    }
}
