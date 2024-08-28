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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.SneakyThrows;

@SpringBootTest(classes = SchemaRegistryHealthIndicator.class)
@ActiveProfiles("kafkaTest")
public class SchemaRegistryHealthIndicatorTest {
    @MockBean
    private SchemaRegistryClient schemaRegistryClient;

    @Autowired
    private SchemaRegistryHealthIndicator schemaRegistryHealthIndicator;

    @Test
    @SneakyThrows
    void whenSchemaRegistryIsAvailable_shouldHealthBeUp() {
        doReturn(new ArrayList<>()).when(schemaRegistryClient).getAllSubjects();
        final Health schemaRegistryActualHealth = schemaRegistryHealthIndicator.health();
        assertThat(schemaRegistryActualHealth.getStatus().getCode())
                .isEqualTo("UP");
    }

    @Test
    @SneakyThrows
    void whenSchemaRegistryIsNotAvailableWithRestClientException_shouldHealthBeDown() {
        doThrow(new RestClientException("Error occurred while querying Schema Registry. Response is 500.", 500, 500))
                .when(schemaRegistryClient).getAllSubjects();
        final Health schemaRegistryActualHealth = schemaRegistryHealthIndicator.health();
        assertThat(schemaRegistryActualHealth.getStatus().getCode())
                .isEqualTo("DOWN");
    }

    @Test
    @SneakyThrows
    void whenSchemaRegistryIsNotAvailableWithIoException_shouldHealthBeDown() {
        doThrow(new IOException("IO Exception occurred while querying Schema Registry."))
                .when(schemaRegistryClient).getAllSubjects();
        final Health schemaRegistryActualHealth = schemaRegistryHealthIndicator.health();
        assertThat(schemaRegistryActualHealth.getStatus().getCode())
                .isEqualTo("DOWN");
    }
}
