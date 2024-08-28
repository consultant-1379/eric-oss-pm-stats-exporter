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

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.ericsson.oss.air.pm.stats.exporter.config.dc.DataCatalogBeans;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageBus;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@ActiveProfiles("dcTest")
@SpringBootTest(classes = {DataCatalogHealthIndicator.class, DataCatalogBeans.class})
@MockBean(name = "dataCatalogCache", classes = Map.class)
public class DataCatalogHealthIndicatorTest {

    private static final MockWebServer SERVER = new MockWebServer();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Integer ID = 69;

    @Autowired
    private DataCatalogHealthIndicator dataCatalogHealthIndicator;

    @DynamicPropertySource
    static void dynamicKafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("dc.url", () -> String.format("http://localhost:%s", SERVER.getPort()));
    }

    @BeforeAll
    @SneakyThrows
    static void setup() {
        SERVER.start();
        SERVER.enqueue(new MockResponse()
                .setBody(OBJECT_MAPPER.writeValueAsString(List.of(MessageBus.builder().id(ID).build())))
                .addHeader("Content-Type", "application/json"));
    }

    @AfterAll
    @SneakyThrows
    static void teardown() {
        SERVER.shutdown();
    }

    @Test
    public void health_shouldReturnUp_whenDataCatalogIsHealthy() {
        SERVER.enqueue(new MockResponse().setResponseCode(200).setBody("{\"status\":\"UP\"}"));
        final Health health = dataCatalogHealthIndicator.health();
        assertThat(health.getStatus())
                .isEqualTo(Status.UP);
    }

    @Test
    public void health_shouldReturnDown_whenDataCatalogIsUnhealthy() {
        SERVER.enqueue(new MockResponse().setResponseCode(500).setBody("{\"status\":\"DOWN\"}"));
        final Health health = dataCatalogHealthIndicator.health();
        assertThat(health.getStatus())
                .isEqualTo(Status.DOWN);
    }
}
