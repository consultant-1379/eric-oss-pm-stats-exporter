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

package com.ericsson.oss.air.pm.stats.exporter.config.dc;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageBus;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjectsV2.DataTypeDto;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjectsV2.MessageSchemaDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ActiveProfiles("dcTest")
@SpringBootTest(classes = DataCatalogBeans.class)
public class DataCatalogBeansTest {

    private static final MockWebServer MOCK_WEB_SERVER = new MockWebServer();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Integer ID = 69;
    private static final String SCHEMA_NAME = "schema name";
    private static final String SCHEMA_VERSION = "69";
    private static final String CACHE_KEY = "schema name:69";

    @Autowired
    private Integer messageBusId;

    @Autowired
    @Qualifier("dataCatalogCache")
    private List<String> dataCatalogCache;

    @DynamicPropertySource
    static void dynamicKafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("dc.url", () -> String.format("http://localhost:%s", MOCK_WEB_SERVER.getPort()));
    }

    @BeforeAll
    @SneakyThrows
    static void setup() {
        MOCK_WEB_SERVER.start();
        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setBody(OBJECT_MAPPER.writeValueAsString(List.of(MessageBus.builder().id(ID).build())))
                .addHeader("Content-Type", "application/json"));
        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setBody(OBJECT_MAPPER.writeValueAsString(List.of(
                    new MessageSchemaDto(new DataTypeDto(SCHEMA_NAME, SCHEMA_VERSION)))))
                .addHeader("Content-Type", "application/json"));
    }

    @AfterAll
    @SneakyThrows
    static void teardown() {
        MOCK_WEB_SERVER.shutdown();
    }

    @Test
    void whenMessageBusAvailable_shouldIdBeRetrievedCorrectly() {
        assertThat(messageBusId)
                .isEqualTo(ID);
    }

    @Test
    void whenSchemaAlreadyRegistered_shouldCacheContainValue() {
        assertThat(dataCatalogCache)
                .contains(CACHE_KEY);
    }
}
