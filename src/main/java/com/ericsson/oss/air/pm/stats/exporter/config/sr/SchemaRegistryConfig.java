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

package com.ericsson.oss.air.pm.stats.exporter.config.sr;

import static io.confluent.kafka.schemaregistry.CompatibilityLevel.FULL;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.NonNull;

/**
 * Configuration for the Schema Registry.
 */
@Configuration
@ConditionalOnProperty("kafka.enabled")
public class SchemaRegistryConfig {

    @Value("${kafka.properties.schema.registry.url}")
    String schemaRegistryUrl;

    /**
     * Retrieve the schema registry client.
     * @return the client of schema registry
     */
    @Bean
    public SchemaRegistryClient schemaRegistryClient() {
        return new CachedSchemaRegistryClient(schemaRegistryUrl, 3);
    }

    /**
     * Update the compatibility with level of FULL.
     * @param subject subject to update
     * @return response of the update compatibility from the schema registry client
     */
    public String updateCompatibilityFull(@NonNull final String subject) throws IOException, RestClientException {
        return schemaRegistryClient().updateCompatibility(subject, FULL.name());
    }
}
