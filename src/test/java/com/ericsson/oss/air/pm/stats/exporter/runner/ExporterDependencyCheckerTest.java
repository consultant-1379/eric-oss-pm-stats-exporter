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

package com.ericsson.oss.air.pm.stats.exporter.runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.sql.DataSource;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.ActiveProfiles;

import lombok.SneakyThrows;

@ActiveProfiles("kafkaTest")
@SpringBootTest(classes = ExporterDependencyChecker.class)
@MockBeans({@MockBean(KafkaAdmin.class), @MockBean(RetryTemplate.class)})
public class ExporterDependencyCheckerTest {
    private static final String COMPLETED_TIMESTAMP_LISTENER_ID = "completedTimestampListenerId";
    private static final AdminClient ADMIN_CLIENT_MOCK = mock(AdminClient.class);
    private static final ListTopicsResult LIST_TOPICS_RESULT_MOCK = mock(ListTopicsResult.class);
    private static final KafkaFuture<Set<String>> KAFKA_FUTURE_MOCK = mock(KafkaFuture.class);

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopicName;

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTsTopicName;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistryMock;

    @MockBean
    private DataSource dataSourceMock;

    @MockBean
    private CountDownLatch innerStateRestorationLatchMock;

    @Autowired
    private ExporterDependencyChecker exporterDependencyChecker;

    @BeforeEach
    void setup() {
        clearInvocations(KAFKA_FUTURE_MOCK);
        clearInvocations(LIST_TOPICS_RESULT_MOCK);
        clearInvocations(ADMIN_CLIENT_MOCK);
        clearInvocations(dataSourceMock);
    }

    @Test
    void whenEveryDependencyHealthy_shouldApplicationStart() {
        doReturn(mock(MessageListenerContainer.class)).when(
            kafkaListenerEndpointRegistryMock).getListenerContainer(COMPLETED_TIMESTAMP_LISTENER_ID);
        exporterDependencyChecker.checkDependencies();
        assertThat(innerStateRestorationLatchMock.getCount())
                .isZero();
    }

    @Test
    @SneakyThrows
    void whenAllTopicsPresent_shouldCheckTopicsFinish() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            adminClientMockedStatic.when(() -> AdminClient.create(anyMap())).thenReturn(ADMIN_CLIENT_MOCK);
            doReturn(LIST_TOPICS_RESULT_MOCK).when(ADMIN_CLIENT_MOCK).listTopics();
            doReturn(KAFKA_FUTURE_MOCK).when(LIST_TOPICS_RESULT_MOCK).names();
            doReturn(Set.of(completedTsTopicName, executionReportTopicName, backupTopicName)).when(
                    KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));

            exporterDependencyChecker.checkTopics.doWithRetry(null);

            verify(KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));
            verify(LIST_TOPICS_RESULT_MOCK).names();
            verify(ADMIN_CLIENT_MOCK).listTopics();
            adminClientMockedStatic.verify(() -> AdminClient.create(anyMap()));
        }
    }

    @Test
    @SneakyThrows
    void whenMissingTopics_shouldIllegalStateExceptionBeThrown() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            adminClientMockedStatic.when(() -> AdminClient.create(anyMap())).thenReturn(ADMIN_CLIENT_MOCK);
            doReturn(LIST_TOPICS_RESULT_MOCK).when(ADMIN_CLIENT_MOCK).listTopics();
            doReturn(KAFKA_FUTURE_MOCK).when(LIST_TOPICS_RESULT_MOCK).names();
            doReturn(Set.of(completedTsTopicName, executionReportTopicName)).when(KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));

            assertThatThrownBy(() -> exporterDependencyChecker.checkTopics.doWithRetry(null))
                    .hasMessage(String.format("Missing topics: [%s]", backupTopicName))
                    .isInstanceOf(IllegalStateException.class);

            verify(KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));
            verify(LIST_TOPICS_RESULT_MOCK).names();
            verify(ADMIN_CLIENT_MOCK).listTopics();
            adminClientMockedStatic.verify(() -> AdminClient.create(anyMap()));
        }
    }

    @Test
    @SneakyThrows
    void whenExceptionThrownByListTopics_shouldIllegalStateExceptionBeThrown() {
        try (MockedStatic<AdminClient> adminClientMockedStatic = mockStatic(AdminClient.class)) {
            adminClientMockedStatic.when(() -> AdminClient.create(anyMap())).thenReturn(ADMIN_CLIENT_MOCK);
            doReturn(LIST_TOPICS_RESULT_MOCK).when(ADMIN_CLIENT_MOCK).listTopics();
            doReturn(KAFKA_FUTURE_MOCK).when(LIST_TOPICS_RESULT_MOCK).names();
            doThrow(ExecutionException.class).when(KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));

            assertThatThrownBy(() -> exporterDependencyChecker.checkTopics.doWithRetry(null))
                    .hasMessage("Checking topics was interrupted by an exception")
                    .isInstanceOf(IllegalStateException.class);

            verify(KAFKA_FUTURE_MOCK).get(anyLong(), any(TimeUnit.class));
            verify(LIST_TOPICS_RESULT_MOCK).names();
            verify(ADMIN_CLIENT_MOCK).listTopics();
            adminClientMockedStatic.verify(() -> AdminClient.create(anyMap()));
        }
    }

    @Test
    @SneakyThrows
    void whenDatasourceConnectionAvailable_shouldCheckPostgresConnectionComplete() {
        exporterDependencyChecker.checkPostgresConnection.doWithRetry(null);
        verify(dataSourceMock).getConnection();
    }

    @Test
    @SneakyThrows
    void whenDatasourceConnectionNotAvailable_shouldIllegalStateExceptionBeThrown() {
        doThrow(SQLException.class).when(dataSourceMock).getConnection();

        assertThatThrownBy(() -> exporterDependencyChecker.checkPostgresConnection.doWithRetry(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Connecting to database was interrupted by an exception");

        verify(dataSourceMock).getConnection();
    }
}
