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

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * Class to determine the availability of the Schema Registry.
 */
@Component("schema-registry")
@ConditionalOnProperty("kafka.enabled")
public class SchemaRegistryHealthIndicator implements HealthIndicator {

    @Autowired
    SchemaRegistryClient schemaRegistryClient;

    /**
     * Method to determine if the Schema Registry is available.
     */
    @Override
    @SuppressWarnings("squid:S1166")
    public Health health() {
        final String schemaRegistryUrlSubjectsEndPoint = "/subjects";
        try {
            schemaRegistryClient.getAllSubjects();
            return Health.up().withDetail("query url", schemaRegistryUrlSubjectsEndPoint)
                    .withDetail("query method", "GET").build();
        } catch (IOException | RestClientException e) {
            return Health.down().withDetail("query url", schemaRegistryUrlSubjectsEndPoint)
                    .withDetail("query method", "GET").withDetail("error", e.getMessage()).build();
        }
    }
}
