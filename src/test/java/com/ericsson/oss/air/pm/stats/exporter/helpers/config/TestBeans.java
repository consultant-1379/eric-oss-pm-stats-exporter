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

package com.ericsson.oss.air.pm.stats.exporter.helpers.config;

import java.util.Collections;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.TopicBuilder;

import io.confluent.kafka.schemaregistry.SchemaProvider;
import io.confluent.kafka.schemaregistry.avro.AvroSchemaProvider;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@TestConfiguration
public class TestBeans {
    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopicName;

    /**
     * Bean to create the execution-report topic for the tests.
     * @return NewTopic used by the AdminClient bean
     */
    @Bean
    public NewTopic topicForExecutionReport() {
        return TopicBuilder.name(executionReportTopicName)
            .partitions(1)
            .replicas(1)
            .build();
    }

    /**
     * Bean overriding the SchemaRegistry client, so it can be mocked out (no real sr instance needed in tests).
     * @return MockSchemaRegistryClient instance
     */
    @Bean
    @Primary
    public SchemaRegistryClient schemaRegistryClient() {
        final SchemaProvider provider = new AvroSchemaProvider();
        return new MockSchemaRegistryClient(Collections.singletonList(provider));
    }

    /**
     * Bean to create MeterRegistry for test.
     * @return MeterRegistry instance
     */
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }
}
