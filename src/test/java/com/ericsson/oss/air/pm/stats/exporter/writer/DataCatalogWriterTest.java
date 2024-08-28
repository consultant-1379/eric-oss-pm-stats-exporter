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

package com.ericsson.oss.air.pm.stats.exporter.writer;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.Charset;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.ericsson.oss.air.pm.stats.exporter.config.dc.DataCatalogBeans;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.CreateSchemaCommand;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataType;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageBus;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageSchemaResponse;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dcTest")
@SpringBootTest(classes = {DataCatalogBeans.class, DataCatalogWriter.class})
public class DataCatalogWriterTest {

    private static final MockWebServer MOCK_WEB_SERVER = new MockWebServer();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Integer ID = 69;
    private static final String SCHEMA_NAME = "schema name";
    private static final Integer SCHEMA_VERSION = 69;
    private static final String CACHE_KEY = "schema name:69";
    private static final String TOPIC_NAME = "topic";

    @Autowired
    private DataCatalogWriter dataCatalogWriter;

    @MockBean
    @Qualifier("dataCatalogCache")
    private List<String> dataCatalogCache;

    @MockBean
    private MeterRegistryHelper meterRegistryHelper;

    @MockBean
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

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
    }

    @AfterAll
    @SneakyThrows
    static void teardown() {
        MOCK_WEB_SERVER.shutdown();
    }

    @Test
    void whenCreateSchemaCalledIsCached_noCallShouldBeMade() {
        final int requestCount = MOCK_WEB_SERVER.getRequestCount();
        Mockito.when(dataCatalogCache.contains(CACHE_KEY)).thenReturn(true);
        dataCatalogWriter.createSchema(TOPIC_NAME, SCHEMA_NAME, SCHEMA_VERSION);
        assertThat(MOCK_WEB_SERVER.getRequestCount())
                .isEqualTo(requestCount);
    }

    @Test
    @SneakyThrows
    void whenCreateSchemaCalledIsNotCachedAndRegistered_oneCallBeMadeAndSchemaBeCached() {
        final int requestCount = MOCK_WEB_SERVER.getRequestCount();
        MOCK_WEB_SERVER.takeRequest();

        Mockito.when(dataCatalogCache.contains(CACHE_KEY)).thenReturn(false);
        MOCK_WEB_SERVER.enqueue(new MockResponse()
                .setBody(OBJECT_MAPPER.writeValueAsString(new MessageSchemaResponse(new DataType(SCHEMA_NAME, SCHEMA_VERSION))))
                .addHeader("Content-Type", "application/json"));

        dataCatalogWriter.createSchema(TOPIC_NAME, SCHEMA_NAME, SCHEMA_VERSION);

        assertThat(MOCK_WEB_SERVER.getRequestCount())
                .isEqualTo(requestCount + 1);
        Mockito.verify(dataCatalogCache).contains(CACHE_KEY);
        Mockito.verify(dataCatalogCache).add(CACHE_KEY);

        final RecordedRequest recordedRequest = MOCK_WEB_SERVER.takeRequest();
        assertThat(recordedRequest.getMethod())
                .isEqualTo("PUT");

        final CreateSchemaCommand createSchemaCommand =
                OBJECT_MAPPER.readValue(recordedRequest.getBody().readString(Charset.defaultCharset()), CreateSchemaCommand.class);
        assertThat(createSchemaCommand.getMessageSchema().getSpecificationReference())
                .isEqualTo(SCHEMA_NAME);
    }
}
