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

import static com.ericsson.oss.air.pm.stats.exporter.processor.report.ScheduledExecutionReportProcessor.NO_AGGREGATION_PERIOD_VALUE;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIMENSIONS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.HOURLY;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI3;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPIS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI_CELL_SECTOR_60;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.ON_DEMAND;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.KafkaContainerUtils.createConsumer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
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
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestBeans;
import com.ericsson.oss.air.pm.stats.exporter.helpers.model.Consumer;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.KpiState;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;
import com.ericsson.oss.air.pm.stats.exporter.reader.KafkaReader;
import com.ericsson.oss.air.pm.stats.exporter.writer.KafkaWriter;
import com.google.common.collect.ImmutableMap;

import lombok.SneakyThrows;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@ActiveProfiles("kafkaTest")
@ContextConfiguration(classes = TestBeans.class)
@Testcontainers
@DirtiesContext
public class InnerStateE2ETestIT {
    private static final Long   TS_2022_05_17_11_00_00 = 1652785200L;
    private static final String TS_2022_05_17_11_00_00_S = "1652785200";
    private static final Long   TS_2022_05_17_14_00_00 = 1652796000L;
    private static final Long   TS_2022_05_17_15_00_00 = 1652799600L;
    private static final String EXECUTION_REPORT_LISTENER_ID = "executionReportListenerId";
    private static final String BACKUP_TOPIC_NAME = "pm-stats-exporter-json-backup-test";
    private static final String COMPLETED_TIMESTAMPS_TOPIC_NAME = "pm-stats-exporter-json-completed-timestamp-test";
    private static final String BAD_MESSAGE = "BAD MESSAGE";

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    private static AdminClient adminClient;
    private static KafkaTemplate<String, ExecutionReport> executionReportKafkaTemplate;
    private static KafkaTemplate<String, Object> backupTopicKafkaTemplate;
    private static KafkaMessageListenerContainer<String, Object> backupTopicMessageListenerContainer;
    private static Consumer backupTopicConsumer;
    private static KafkaMessageListenerContainer<String, Object> completedTimestampsTopicMessageListenerContainer;
    private static Consumer completedTimestampsTopicConsumer;

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopic;

    @Value("${kafka.topics.execution-report.group-id}")
    private String executionReportGroupId;

    @Autowired
    private Status status;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @SpyBean
    private KafkaWriter writer;

    @SpyBean
    private KafkaReader kafkaReader;

    @DynamicPropertySource
    static void dynamicKafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
    }

    @BeforeEach
    void clear() throws SQLException {
        doNothing().when(kafkaReader).receiveOnCompletedTimestampTopic(any(ConsumerRecord.class));
    }

    @BeforeAll
    static void setup() {
        adminClient = AdminClient.create(
                ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()));

        executionReportKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                new HashMap<>(KafkaTestUtils.producerProps(KAFKA_CONTAINER.getBootstrapServers())),
                new StringSerializer(), new JsonSerializer<>()));
        backupTopicKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                new HashMap<>(KafkaTestUtils.producerProps(KAFKA_CONTAINER.getBootstrapServers())),
                new StringSerializer(), new JsonSerializer<>()));

        backupTopicMessageListenerContainer = createConsumer(BACKUP_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), true);
        backupTopicConsumer = (Consumer) backupTopicMessageListenerContainer.getContainerProperties().getMessageListener();
        completedTimestampsTopicMessageListenerContainer =
                createConsumer(COMPLETED_TIMESTAMPS_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), true);
        completedTimestampsTopicConsumer =
                (Consumer) completedTimestampsTopicMessageListenerContainer.getContainerProperties().getMessageListener();
    }

    @AfterAll
    static void teardown() {
        backupTopicMessageListenerContainer.stop();
        completedTimestampsTopicMessageListenerContainer.stop();
        executionReportKafkaTemplate.getProducerFactory().reset();
        backupTopicKafkaTemplate.getProducerFactory().reset();
    }

    @BeforeEach
    void setupStatus() {
        kafkaListenerEndpointRegistry.getListenerContainer(EXECUTION_REPORT_LISTENER_ID).start();
        final Status localStatus = new Status();
        final TableStatus tableStatus = new TableStatus();
        tableStatus.setAggregationPeriod(HOURLY);
        tableStatus.setListOfDimensions(new ArrayList<>(DIMENSIONS));
        tableStatus.setKpis(new HashMap<>(Map.of(
                KPI1, new KpiState(true, TS_2022_05_17_11_00_00_S, TS_2022_05_17_11_00_00_S),
                KPI2, new KpiState(true, TS_2022_05_17_11_00_00_S, TS_2022_05_17_11_00_00_S),
                KPI3, new KpiState(true, TS_2022_05_17_11_00_00_S, TS_2022_05_17_11_00_00_S))));
        tableStatus.setLastExported(TS_2022_05_17_11_00_00_S);
        localStatus.put(KPI_CELL_SECTOR_60, tableStatus);
        BeanUtils.copyProperties(localStatus, status);
    }

    @Test
    @SneakyThrows
    void whenCorrectExecutionReportReceivedButNoExportTriggered_shouldBackupBeSavedOffsetCommittedAndNoTimestampBeExported() {
        final long offset = getOffset();

        executionReportKafkaTemplate.send(executionReportTopic,
                buildExecutionReport(List.of(TS_2022_05_17_11_00_00,
                        TS_2022_05_17_12_00_00,
                        TS_2022_05_17_11_00_00)));

        final Status receivedStatus = (Status) backupTopicConsumer.getNext(5, TimeUnit.SECONDS);
        assertThat(receivedStatus)
                .isNotNull();

        final TableStatus tableStatus = receivedStatus.get(KPI_CELL_SECTOR_60);
        assertThat(tableStatus.getKpis().get(KPI1).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_11_00_00_S);
        assertThat(tableStatus.getKpis().get(KPI2).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_12_00_00_S);
        assertThat(tableStatus.getKpis().get(KPI3).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_11_00_00_S);

        assertThat(completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @SneakyThrows
    void whenOneTableReceivedWithNegativeOneAggregationPeriod_shouldNotBackupBeSavedAndNoTimestampBeExportedButOffsetIsCommitted() {
        final long offset = getOffset();

        final ExecutionReport executionReport = buildExecutionReport(List.of(TS_2022_05_17_12_00_00,
                                                                            TS_2022_05_17_12_00_00,
                                                                            TS_2022_05_17_12_00_00));

        executionReport.getTables().get(0).setAggregationPeriod(NO_AGGREGATION_PERIOD_VALUE);

        final Status localStatus = new Status();
        BeanUtils.copyProperties(localStatus, status);

        executionReportKafkaTemplate.send(executionReportTopic, executionReport);

        final Status receivedStatus = (Status) backupTopicConsumer.getNext(5, TimeUnit.SECONDS);

        assertThat(receivedStatus.getStatusMap())
                .isEmpty();

        assertThat(completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @SneakyThrows
    void whenCorrectExecutionReportReceivedAndReliabilityThresholdIncrementsByOne_shouldBackupBeSavedOffsetCommittedAndOneTimestampBeExported() {
        final long offset = getOffset();

        executionReportKafkaTemplate.send(executionReportTopic,
                buildExecutionReport(List.of(TS_2022_05_17_12_00_00,
                        TS_2022_05_17_12_00_00, TS_2022_05_17_12_00_00)));

        final Status receivedStatus = (Status) backupTopicConsumer.getNext(5, TimeUnit.SECONDS);
        assertThat(receivedStatus)
                .isNotNull();

        final TableStatus tableStatus = receivedStatus.get(KPI_CELL_SECTOR_60);
        final Map<String, KpiState> expectedKpis = new HashMap<>();
        expectedKpis.put(KPI1, new KpiState(true, TS_2022_05_17_12_00_00_S, TS_2022_05_17_12_00_00_S));
        expectedKpis.put(KPI2, new KpiState(true, TS_2022_05_17_12_00_00_S, TS_2022_05_17_12_00_00_S));
        expectedKpis.put(KPI3, new KpiState(true, TS_2022_05_17_12_00_00_S, TS_2022_05_17_12_00_00_S));

        assertThat(tableStatus.getKpis())
                .allSatisfy((kpiName, kpi) -> assertThat(kpi.getReliabilityThreshold())
                        .isEqualTo(expectedKpis.get(kpiName).getReliabilityThreshold()));

        final ColumnsAndTimeStamps columnsAndTimeStamps = (ColumnsAndTimeStamps) completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS);
        final ColumnsAndTimeStamps expectedCts = new ColumnsAndTimeStamps(
                DIMENSIONS,
                KPIS,
                SCHEDULED,
                TS_2022_05_17_11_00_00_S,
                TS_2022_05_17_12_00_00_S,
                KPI_CELL_SECTOR_60,
                EXECUTION_ID
        );

        assertThat(columnsAndTimeStamps)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedCts);

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @SneakyThrows
    void whenCorrectExecutionReportReceivedAndReliabilityThresholdIncrementsByTwo_shouldBackupBeSavedOffsetCommittedAndAllTimestampsBeExported() {
        final long offset = getOffset();

        executionReportKafkaTemplate.send(executionReportTopic,
                buildExecutionReport(List.of(TS_2022_05_17_13_00_00,
                        TS_2022_05_17_13_00_00, TS_2022_05_17_13_00_00)));

        final Status receivedStatus = (Status) backupTopicConsumer.getNext(5, TimeUnit.SECONDS);
        assertThat(receivedStatus)
                .isNotNull();

        final TableStatus tableStatus = receivedStatus.get(KPI_CELL_SECTOR_60);
        assertThat(tableStatus.getKpis().get(KPI1).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_13_00_00_S);
        assertThat(tableStatus.getKpis().get(KPI2).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_13_00_00_S);
        assertThat(tableStatus.getKpis().get(KPI3).getReliabilityThreshold())
                .isEqualTo(TS_2022_05_17_13_00_00_S);

        final ColumnsAndTimeStamps columnsAndTimeStamps = (ColumnsAndTimeStamps) completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS);
        final ColumnsAndTimeStamps expected = new ColumnsAndTimeStamps(
                DIMENSIONS,
                KPIS,
                SCHEDULED,
                TS_2022_05_17_11_00_00_S,
                TS_2022_05_17_12_00_00_S,
                KPI_CELL_SECTOR_60,
                EXECUTION_ID
        );

        assertThat(columnsAndTimeStamps)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);

        final ColumnsAndTimeStamps columnsAndTimeStamps2 = (ColumnsAndTimeStamps) completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS);
        final ColumnsAndTimeStamps expected2 = new ColumnsAndTimeStamps(
                DIMENSIONS,
                KPIS,
                SCHEDULED,
                TS_2022_05_17_12_00_00_S,
                TS_2022_05_17_13_00_00_S,
                KPI_CELL_SECTOR_60,
                EXECUTION_ID
        );

        assertThat(columnsAndTimeStamps2)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected2);

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    @Test
    @SneakyThrows
    @Transactional("innerStateTransactionManager")
    void whenExecutionReportReceivedAndExceptionHappens_shouldBackupAndCompletedTimestampsTopicBeEmptyAndOffsetNotBeCommitted() {
        doThrow(RollBackException.class).when(writer).sendJson(anyString(), anyString(), any(Status.class));

        final long offset = getOffset();

        executionReportKafkaTemplate.send(executionReportTopic,
                buildExecutionReport(List.of(TS_2022_05_17_11_00_00,
                        TS_2022_05_17_11_00_00, TS_2022_05_17_11_00_00)));

        assertThat(backupTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        assertThat(completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        await().during(5, TimeUnit.SECONDS).atMost(6, TimeUnit.SECONDS).until(() -> !isOffsetCommitted(offset));

        doCallRealMethod().when(writer).sendJson(anyString(), anyString(), any(Status.class));
        backupTopicConsumer.flush();
        completedTimestampsTopicConsumer.flush();
    }

    @Test
    @SneakyThrows
    void whenExecutionReportReceivedInWrongFormat_shouldBackupAndCompletedTimestampsTopicBeEmptyButOffsetBeCommitted(final CapturedOutput output) {
        final long offset = getOffset();

        final KafkaTemplate<String, String> template = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                new HashMap<>(KafkaTestUtils.producerProps(KAFKA_CONTAINER.getBootstrapServers())),
                new StringSerializer(), new JsonSerializer<>()));

        template.send(executionReportTopic, BAD_MESSAGE);

        assertThat(backupTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        assertThat(completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS))
                .isNull();
        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
        assertThat(output.getOut())
                .contains(String.format("Received invalid message on topic '%s': ", executionReportTopic));
    }

    @Test
    @SneakyThrows
    void whenCorrectOnDemandExecutionReportReceived_shouldTimeStampExportedAndOffsetCommitted() {
        final long offset = getOffset();

        final ExecutionReport report = buildExecutionReport(List.of(TS_2022_05_17_11_00_00,
                TS_2022_05_17_12_00_00,
                TS_2022_05_17_11_00_00));

        report.setScheduled(ON_DEMAND);

        executionReportKafkaTemplate.send(executionReportTopic, report);

        final Status receivedStatus = (Status) backupTopicConsumer.getNext(5, TimeUnit.SECONDS);
        assertThat(receivedStatus)
                .isNull();

        final ColumnsAndTimeStamps columnsAndTimeStamps = (ColumnsAndTimeStamps) completedTimestampsTopicConsumer.getNext(5, TimeUnit.SECONDS);
        final ColumnsAndTimeStamps expected = new ColumnsAndTimeStamps(
                DIMENSIONS,
                KPIS,
                ON_DEMAND,
                TS_2022_05_17_11_00_00_S,
                TS_2022_05_17_12_00_00_S,
                KPI_CELL_SECTOR_60,
                EXECUTION_ID
        );

        assertThat(columnsAndTimeStamps)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);

        await().atMost(5, TimeUnit.SECONDS).until(() -> isOffsetCommitted(offset));
    }

    /**
     * Helper method to retrieve the offset value for the execution report topic from a consumer group using an adminClient object.
     *
     * @return offset value for the execution report topic.
     */
    @SneakyThrows
    private long getOffset() {
        final TopicPartition topicPartition = new TopicPartition(executionReportTopic, 0);
        final OffsetAndMetadata offsetAndMetadata =
                adminClient.listConsumerGroupOffsets(executionReportGroupId).partitionsToOffsetAndMetadata().get().get(topicPartition);
        return offsetAndMetadata == null ? 0 : offsetAndMetadata.offset();
    }

    /**
     * Helper method to determine if the offset of the execution report topic has been increased after the first transaction.
     *
     * @param offset The offset value before exporting happened.
     * @return boolean value which shows if the offset of the execution report topic has been increased after the first transaction.
     */
    @SneakyThrows
    private boolean isOffsetCommitted(final long offset) {
        final TopicPartition topicPartition = new TopicPartition(executionReportTopic, 0);
        final OffsetAndMetadata offsetAndMetadata =
                adminClient.listConsumerGroupOffsets(executionReportGroupId).partitionsToOffsetAndMetadata().get().get(topicPartition);
        return offsetAndMetadata != null && offsetAndMetadata.offset() > offset;
    }

    /**
     * Helper method to build execution reports for the tests.
     *
     * @param reliabilityThresholds<long> list of reliability thresholds for helping to build the execution report.
     * @return boolean value which shows if the offset of the execution report topic has been increased after the first transaction.
     */
    private ExecutionReport buildExecutionReport(final List<Long> reliabilityThresholds) {
        final ExecutionReport report = buildDefaultExecutionReport();

        report.setScheduled(SCHEDULED);
        report.setExecutionStart(TS_2022_05_17_14_00_00);
        report.setExecutionEnd(TS_2022_05_17_15_00_00);

        final List<ExecutionReportKpi> localKpis = new ArrayList<>();
        IntStream.range(1, 4).forEach(i -> {
            final ExecutionReportKpi kpi = new ExecutionReportKpi();
            kpi.setName(String.format("kpi%d", i));
            kpi.setReexportLateDataEnabled(false);
            kpi.setExportable(true);
            kpi.setReliabilityThreshold(reliabilityThresholds.size() < i ? TS_2022_05_17_11_00_00 : reliabilityThresholds.get(i - 1));
            kpi.setCalculationStartTime(reliabilityThresholds.size() < i ? TS_2022_05_17_11_00_00 : reliabilityThresholds.get(i - 1));
            localKpis.add(kpi);
        });

        report.setTables(new ArrayList<>(List.of(new Table(KPI_CELL_SECTOR_60, HOURLY, List.of(DIM1, DIM2), List.of(KPI1, KPI2, KPI3), localKpis))));

        return report;
    }
}
