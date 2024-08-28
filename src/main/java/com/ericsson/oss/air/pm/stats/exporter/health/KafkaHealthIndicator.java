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

package com.ericsson.oss.air.pm.stats.exporter.health;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.Uninterruptibles;

/**
 * Class to determine the availability of Kafka.
 */
@Component("kafka-health")
@ConditionalOnProperty("kafka.enabled")
public class KafkaHealthIndicator implements HealthIndicator {

    @Autowired
    private KafkaAdmin kafkaAdmin;

    /**
     * Method to determine if Kafka is available.
     */
    @Override
    @SuppressWarnings({"squid:S1166"})
    public Health health() {
        try (AdminClient client = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
            Uninterruptibles.getUninterruptibly(client.listTopics(new ListTopicsOptions().timeoutMs(100)).names(), 100, TimeUnit.MILLISECONDS);
        } catch (final KafkaException | ExecutionException | TimeoutException e) {
            return Health.down().withDetail("query method", "AdminClient::listTopics").withDetail("error", e.toString()).build();
        }
        return Health.up().withDetail("query method", "AdminClient::listTopics").build();
    }
}
