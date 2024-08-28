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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestBeans;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import lombok.SneakyThrows;


@SpringBootTest(classes = SchemaRegistryConfig.class)
@ContextConfiguration(classes = TestBeans.class)
@ActiveProfiles("kafkaTest")
class SchemaRegistryConfigTest {

    private static final String TEST_SUBJECT = "test_subject";

    @Autowired
    private SchemaRegistryClient schemaRegistry;

    @Autowired
    private SchemaRegistryConfig schemaRegistryConfig;

    @Test
    @SneakyThrows
    public void whenSchemaRegistryClientUpdateCompatibilityCalled_shouldReturnExpectedResponse() {
        assertThat(schemaRegistry.updateCompatibility(TEST_SUBJECT, "full"))
                .isEqualTo("full");
    }

    @Test
    @SneakyThrows
    public void whenSchemaRegistryConfigUpdateCompatibilityFullCalled_shouldReturnFull()  {
        assertThat(schemaRegistryConfig.updateCompatibilityFull(TEST_SUBJECT))
                .isEqualTo("FULL");
    }

    @Test
    void whenApplicationStarts_shouldSSchemaRegistryClientBeCreated() {
        assertThat(schemaRegistryConfig.schemaRegistryClient())
                .isInstanceOf(MockSchemaRegistryClient.class);
    }
}