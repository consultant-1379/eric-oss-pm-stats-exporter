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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.context.ActiveProfiles;

import lombok.SneakyThrows;

@SpringBootTest(classes = KafkaHealthIndicator.class)
@ActiveProfiles("kafkaTest")
@MockBean(KafkaAdmin.class)
public class KafkaHealthIndicatorTest {
    private static final AdminClient ADMIN_CLIENT = mock(AdminClient.class);
    private static final KafkaFuture<Set<String>> FUTURE = mock(KafkaFuture.class);
    private static final ListTopicsResult RESULT = mock(ListTopicsResult.class);
    @Autowired
    private KafkaHealthIndicator kafkaHealthIndicator;

    @SneakyThrows
    @Test
    void whenKafkaIsAvailable_shouldHealthBeUp() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            doReturn(new ArrayList<>()).when(FUTURE).get(anyLong(), any(TimeUnit.class));
            doReturn(FUTURE).when(RESULT).names();
            doReturn(RESULT).when(ADMIN_CLIENT).listTopics(any(ListTopicsOptions.class));
            adminClientMockedStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(ADMIN_CLIENT);

            final Health kafkaActualHealth = kafkaHealthIndicator.health();
            assertThat(kafkaActualHealth.getStatus().getCode())
                    .isEqualTo("UP");
        }
    }

    @SneakyThrows
    @Test
    void whenKafkaIsNotAvailable_shouldExecutionExceptionAndKafkaExceptionBeThrownAndHealthBeDown() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            doThrow(new ExecutionException("Kafka is unavailable.",
                    new KafkaException("Error occurred while trying to reach Kafka."))).when(FUTURE).get(anyLong(), any(TimeUnit.class));
            doReturn(FUTURE).when(RESULT).names();
            doReturn(RESULT).when(ADMIN_CLIENT).listTopics(any(ListTopicsOptions.class));
            adminClientMockedStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(ADMIN_CLIENT);

            final Health kafkaActualHealth = kafkaHealthIndicator.health();
            assertThat(kafkaActualHealth.getStatus().getCode())
                    .isEqualTo("DOWN");
        }
    }

    @SneakyThrows
    @Test
    void whenKafkaIsNotAvailable_shouldTimeOutExceptionAndKafkaExceptionBeThrownAndHealthBeDown() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            doThrow(new TimeoutException("Kafka is unavailable.",
                    new KafkaException("Error occurred while trying to reach Kafka."))).when(FUTURE).get(anyLong(), any(TimeUnit.class));
            doReturn(FUTURE).when(RESULT).names();
            doReturn(RESULT).when(ADMIN_CLIENT).listTopics(any(ListTopicsOptions.class));
            adminClientMockedStatic.when(() -> AdminClient.create(any(Map.class))).thenReturn(ADMIN_CLIENT);
            final Health kafkaActualHealth = kafkaHealthIndicator.health();
            assertThat(kafkaActualHealth.getStatus().getCode())
                    .isEqualTo("DOWN");
        }
    }
}
