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

package com.ericsson.oss.air.pm.stats.exporter.runner;

import static com.google.common.collect.Sets.newHashSet;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.sql.DataSource;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.exception.InnerStateException;
import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Checking the service's dependencies.
 */
@Component
@ConditionalOnProperty({"kafka.enabled", "datasource.enabled"})
public class ExporterDependencyChecker {
    private static final String COMPLETED_TIMESTAMP_LISTENER_ID = "completedTimestampListenerId";
    private static final String LISTENER_IS_NULL_ERROR = "Container Listener returned with null for listener id: ";

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopicName;

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTsTopicName;

    @Autowired
    private RetryTemplate kafkaAdminRetryTemplate;

    @Autowired
    private RetryTemplate checkTopicsRetryTemplate;

    @Autowired
    private RetryTemplate postgresConnectionRetryTemplate;

    @Autowired
    private KafkaAdmin admin;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private CountDownLatch innerStateRestorationLatch;

    /**
     * Checks on Kafka that the required topics has been created, waits a few seconds if needed.
     */
    public final RetryCallback checkTopics = arg -> {
        final Set<String> requiredTopics = newHashSet(backupTopicName, completedTsTopicName, executionReportTopicName);
        try (AdminClient adminClient = AdminClient.create(admin.getConfigurationProperties())) {
            try {
                requiredTopics.removeAll(
                        Uninterruptibles.getUninterruptibly(adminClient.listTopics().names(), 2, TimeUnit.SECONDS));
                if (!requiredTopics.isEmpty()) {
                    throw new IllegalStateException("Missing topics: " + requiredTopics.toString());
                }
            } catch (final ExecutionException | TimeoutException e) {
                throw new IllegalStateException("Checking topics was interrupted by an exception", e);
            }
        }
        return null;
    };

    /**
     * Checks if a connection to Postgres could be established.
     */
    public final RetryCallback checkPostgresConnection = arg -> {
        try {
            dataSource.getConnection();
        } catch (final SQLException e) {
            throw new IllegalStateException("Connecting to database was interrupted by an exception", e);
        }
        return null;
    };

    /**
     * Collects all the dependency checkers, and validate, they are available on application startup.
     */
    public void checkDependencies() {
        kafkaAdminRetryTemplate.execute(arg -> admin.initialize(), context -> {
            throw new InnerStateException("Could not initialize kafka admin: ", context.getLastThrowable());
        });

        checkTopicsRetryTemplate.execute(checkTopics, context -> {
            throw new InnerStateException("The topics did not became available: ", context.getLastThrowable());
        });

        postgresConnectionRetryTemplate.execute(checkPostgresConnection, context -> {
            throw new InnerStateException("Could not initialize Postgres connection: ", context.getLastThrowable());
        });

        Optional.ofNullable(kafkaListenerEndpointRegistry.getListenerContainer(COMPLETED_TIMESTAMP_LISTENER_ID)).orElseThrow(() ->
            new InnerStateException(LISTENER_IS_NULL_ERROR + COMPLETED_TIMESTAMP_LISTENER_ID)).start();

        innerStateRestorationLatch.countDown();
    }
}
