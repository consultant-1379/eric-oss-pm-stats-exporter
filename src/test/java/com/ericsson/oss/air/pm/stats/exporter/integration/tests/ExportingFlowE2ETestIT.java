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

package com.ericsson.oss.air.pm.stats.exporter.integration.tests;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DEFAULT_COLUMNS_AND_TIMESTAMPS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIMENSIONS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI3;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPIS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI_CELL_SECTOR_60;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.PG_PASSWORD;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.PG_USERNAME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED_TOPIC_NAME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.KafkaContainerUtils.createAvroConsumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestBeans;
import com.ericsson.oss.air.pm.stats.exporter.helpers.model.Consumer;
import com.ericsson.oss.air.pm.stats.exporter.helpers.model.ExceptionAnswer;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.google.common.collect.ImmutableMap;

import lombok.SneakyThrows;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@ActiveProfiles("e2eTest")
@ContextConfiguration(classes = TestBeans.class)
@Testcontainers
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExportingFlowE2ETestIT {

    private static final String NON_EXISTING_DIMENSION = "nonexistent_dimension";

    private static final String DATABASE_NOT_AVAILABLE_WARNING = "Database is unavailable and connection wasn't restored in time";
    private static final String EMPTY_QUERY_WARNING = "Query returned empty.";
    private static final String BAD_SQL_WARNING = "Problem occurred while querying the SQL.";
    private static final String TS_2022_05_17_14_00_00_S = "1652796000";
    private static final String TIMESTAMP = "timestamp";
    private static final String NON_EXISTING_TABLE_NAME = "kpi_nonExistingTable_60";

    private static final List<Map<String, Object>> DATA_FULL = List.of(
        new HashMap<>(Map.of(DIM1, 4, DIM2, "dimValue1", TIMESTAMP, TS_2022_05_17_12_00_00, KPI1,
            966608473, KPI2, 7818.5965, KPI3, "27476439")),
        new HashMap<>(Map.of(DIM1, 5, DIM2, "dimValue2", TIMESTAMP, TS_2022_05_17_12_00_00, KPI1,
            1113764514, KPI2, 10106.97172, KPI3, "182377383")),
        new HashMap<>(Map.of(DIM1, 6, DIM2, "dimValue3", TIMESTAMP, TS_2022_05_17_12_00_00, KPI1,
            369929604, KPI2, 33247.8488, KPI3, "827534987"))
    );

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = (PostgreSQLContainer) new PostgreSQLContainer("postgres:13.5-alpine")
            .withUsername(PG_USERNAME)
            .withPassword(PG_PASSWORD)
            .withInitScript("data.sql");

    private static AdminClient adminClient;
    private static KafkaTemplate<String, ColumnsAndTimeStamps> columnsAndTimeStampsKafkaTemplate;
    private static KafkaMessageListenerContainer<String, Object> outputTopicMessageListenerContainer;
    private static Consumer outputTopicConsumer;
    private static KafkaMessageListenerContainer<String, Object> outputTopicTransactionalMessageListenerContainer;
    private static Consumer outputTopicTransactionalConsumer;
    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTimestampTopic;

    @Value("${kafka.topics.completed-timestamp.group-id}")
    private String completedTimestampGroupId;

    @SpyBean
    private KafkaTemplate<String, GenericRecord> avroKafkaTemplate;

    @DynamicPropertySource
    static void dynamicKafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("datasource.enabled", () -> true);
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", () -> PG_USERNAME);
        registry.add("spring.datasource.driver-class-name", POSTGRE_SQL_CONTAINER::getDriverClassName);
        registry.add("spring.datasource.password", () -> PG_PASSWORD);
        registry.add("spring.datasource.hikari.connectionTimeout", () -> 250);
        registry.add("kafka.topics.scheduled.topic-name", () -> SCHEDULED_TOPIC_NAME);
    }

    @BeforeAll
    static void setup() {
        adminClient = AdminClient.create(ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()));
        columnsAndTimeStampsKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                new HashMap<>(KafkaTestUtils.producerProps(KAFKA_CONTAINER.getBootstrapServers())),
                new StringSerializer(), new JsonSerializer<>()));
        outputTopicMessageListenerContainer = createAvroConsumer(SCHEDULED_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), false);
        outputTopicConsumer = (Consumer) outputTopicMessageListenerContainer.getContainerProperties().getMessageListener();
        outputTopicTransactionalMessageListenerContainer =
                createAvroConsumer(SCHEDULED_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), true);
        outputTopicTransactionalConsumer =
                (Consumer) outputTopicTransactionalMessageListenerContainer.getContainerProperties().getMessageListener();
    }

    @AfterAll
    static void teardown() {
        outputTopicMessageListenerContainer.stop();
        outputTopicTransactionalMessageListenerContainer.stop();
        columnsAndTimeStampsKafkaTemplate.getProducerFactory().reset();
    }

    @Test
    @Order(1)
    @SneakyThrows
    void whenMessageReceivedOnCompletedTimestampsAndNoError_shouldNecessaryRowsBeExportedOnceAndOffsetBeCommitted() {
        final long offset = getOffset(completedTimestampTopic);

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, DEFAULT_COLUMNS_AND_TIMESTAMPS);

        for (final Map<String, Object> expected: DATA_FULL) {
            final GenericRecord actual = (GenericRecord) outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS);

            assertThat(Stream.concat(DIMENSIONS.stream(), KPIS.stream()))
                    .isNotEmpty()
                    .allSatisfy(column -> assertThat(actual.hasField(column))
                            .isTrue())
                    .allSatisfy(column -> assertThat(actual.get(column))
                            .hasToString(expected.get(column).toString()));

            Stream.concat(DIMENSIONS.stream(), KPIS.stream()).forEach(column -> {
                assertThat(actual.hasField(column))
                        .isTrue();
                assertThat(actual.get(column))
                        .hasToString(expected.get(column).toString());
            });
        }

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
        outputTopicConsumer.flush();
    }

    @Test
    @Order(2)
    @SneakyThrows
    void whenInvalidTimestampReceived_shouldNothingBeExportedButOffsetBeCommitted(final CapturedOutput output) {
        final long offset = getOffset(completedTimestampTopic);

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, new ColumnsAndTimeStamps(
                DIMENSIONS, KPIS, SCHEDULED, TS_2022_05_17_14_00_00_S, TS_2022_05_17_13_00_00_S, KPI_CELL_SECTOR_60,
                EXECUTION_ID
        ));

        assertThat(outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        assertThat(output.getOut())
                .contains(EMPTY_QUERY_WARNING);
        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @Order(3)
    @SneakyThrows
    void whenWrongInputReceivedOnCompletedTimeStampsTopic_shouldNothingBeExportedButOffsetBeCommitted(final CapturedOutput output) {
        final long offset = getOffset(completedTimestampTopic);

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, new ColumnsAndTimeStamps(
                Stream.concat(DIMENSIONS.stream(), Stream.of(NON_EXISTING_DIMENSION)).collect(Collectors.toList()),
                KPIS, SCHEDULED, TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, KPI_CELL_SECTOR_60, EXECUTION_ID
        ));

        assertThat(outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        assertThat(output.getOut())
                .contains(BAD_SQL_WARNING);
        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, new ColumnsAndTimeStamps(
                DIMENSIONS, KPIS, SCHEDULED, TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, NON_EXISTING_TABLE_NAME,
                EXECUTION_ID
        ));

        assertThat(outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        assertThat(output.getOut())
                .contains(BAD_SQL_WARNING);
        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @Order(Integer.MAX_VALUE)
    @DirtiesContext (methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @SneakyThrows
    void whenPostgreNotAvailable_shouldNothingBeExportedAndOffsetNotBeCommitted(final CapturedOutput output) {
        outputTopicConsumer.flush();
        outputTopicTransactionalConsumer.flush();
        POSTGRE_SQL_CONTAINER.stop();

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, DEFAULT_COLUMNS_AND_TIMESTAMPS);

        assertThat(outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        await().atMost(5, TimeUnit.SECONDS).until(() -> output.getOut().contains(DATABASE_NOT_AVAILABLE_WARNING));

        outputTopicTransactionalConsumer.flush(10, TimeUnit.SECONDS);
        outputTopicConsumer.flush();
    }

    @Test
    @Order(4)
    @SneakyThrows
    void whenMessageReceivedOnCompletedTimestampsAndErrorHappens_shouldRowsNotBeExportedAndOffsetNotBeCommitted() {
        doAnswer(new ExceptionAnswer()).when(avroKafkaTemplate).send(anyString(), any(GenericRecord.class));

        final long offset = getOffset(completedTimestampTopic);

        columnsAndTimeStampsKafkaTemplate.send(completedTimestampTopic, DEFAULT_COLUMNS_AND_TIMESTAMPS);

        await().during(5, TimeUnit.SECONDS).atMost(6, TimeUnit.SECONDS).until(() -> {
            final GenericRecord message = (GenericRecord) outputTopicConsumer.getNext(100, TimeUnit.MILLISECONDS);
            return message == null || !message.get(DIM1).equals(6);
        });
        assertThat(outputTopicTransactionalConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        await().during(5, TimeUnit.SECONDS).atMost(6, TimeUnit.SECONDS).until(() -> !isOffsetCommitted(offset));

        doCallRealMethod().when(avroKafkaTemplate).send(anyString(), any(GenericRecord.class));

        outputTopicConsumer.flush();
        outputTopicTransactionalConsumer.flush();
    }

    /**
     * Helper method to retrieve the offset value for the specified topic from a consumer group using an adminClient object.
     *
     * @param topic The name of the topic.
     * @return offset value for the specified topic.
     */
    @SneakyThrows
    private long getOffset(final String topic) {
        final TopicPartition topicPartition = new TopicPartition(topic, 0);
        final OffsetAndMetadata offsetAndMetadata =
                adminClient.listConsumerGroupOffsets(completedTimestampGroupId)
                        .partitionsToOffsetAndMetadata()
                        .get()
                        .get(topicPartition);
        return offsetAndMetadata == null ? 0 : offsetAndMetadata.offset();
    }

    /**
     * Helper method to determine if the offset of the completed time stamp topic has been increased after the exporting
     * procedure.
     *
     * @param offset The offset value before exporting happened.
     * @return boolean value which shows if the offset of the completed time stamp topic has been
     *         increased after the exporting procedure.
     */
    @SneakyThrows
    private boolean isOffsetCommitted(final long offset) {
        final TopicPartition topicPartition = new TopicPartition(completedTimestampTopic, 0);
        final OffsetAndMetadata offsetAndMetadata =
                adminClient.listConsumerGroupOffsets(completedTimestampGroupId)
                        .partitionsToOffsetAndMetadata()
                        .get()
                        .get(topicPartition);
        return offsetAndMetadata != null && offsetAndMetadata.offset() > offset;
    }
}
