/*******************************************************************************
 * COPYRIGHT Ericsson 2024
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

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.KafkaContainerUtils.createAvroConsumer;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.CTS_TOPIC_NAME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.ON_DEMAND;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.PG_PASSWORD;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.PG_USERNAME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED_TOPIC_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
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
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestBeans;
import com.ericsson.oss.air.pm.stats.exporter.helpers.model.Consumer;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;

@SpringBootTest
@ActiveProfiles("e2eTest")
@ContextConfiguration(classes = TestBeans.class)
@Testcontainers
@DirtiesContext
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureObservability
@ExtendWith({OutputCaptureExtension.class})
public class KafkaToKafkaE2ETestIT {
    private static final String NUMBER_OF_REPORTS_FORMAT =
            "pm_stats_exporter_number_of_execution_reports_from_kafka_total{isScheduled=\"%s\",}";
    private static final String NUMBER_OF_TABLES_FORMAT = "pm_stats_exporter_number_of_tables_in_execution_reports_total";
    private static final String NUMBER_OF_DIFFERENT_TABLES_FORMAT =
            "pm_stats_exporter_number_of_different_tables_in_execution_reports_total{isScheduled=\"%s\",}";
    private static final String NUMBER_OF_KPIS_FORMAT = "pm_stats_exporter_number_of_kpis_in_execution_reports_total";
    private static final String NUMBER_OF_RECORDS_PUT_FORMAT = "pm_stats_exporter_number_of_records_put_on_kafka_total{topic=\"%s\",}";
    private static final String OCCURRENCE_OF_TABLE_FORMAT =
            "pm_stats_exporter_occurrence_of_table_in_execution_reports_total{isScheduled=\"%s\",table=\"%s\",}";
    private static final String COMMON_USE_CASE_A = "7-10_setup";
    private static final String ON_DEMAND_TOPIC_NAME = "on-demand-test";
    private static final String KPI_CELL_GUID_60 = "kpi_cell_guid_60";
    private static final String KPI_CELL_SECTOR_60 = "kpi_cell_sector_60";
    private static final String EXECUTION_REPORT_LISTENER_ID = "executionReportListenerId";
    private static final String METRIC_NULL_NULL = "0.0";
    private static final String KPI_CELL_SECTOR_1440 = "kpi_cell_sector_1440";
    private static final String EXECUTION_REPORT_COLLECTOR_METRIC_ID = "pm_stats_exporter_exec_arrive";
    private static final String COMPLETED_TIMESTAMP_COLLECTOR_METRIC_ID = "pm_stats_exporter_cts_avro_export";

    private static final int TIMEOUT = 5;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static KafkaTemplate<String, ExecutionReport> executionReportKafkaTemplate;
    private static KafkaMessageListenerContainer<String, Object> scheduledTopicMessageListenerContainer;
    private static Consumer scheduledTopicConsumer;
    private static KafkaMessageListenerContainer<String, Object> onDemandTopicMessageListenerContainer;
    private static Consumer onDemandTopicConsumer;

    private static final String KEY_ID = "id";
    private static final String KEY_TABLE = "table";
    private static final String KEY_KPI = "kpi";
    private static final String KEY_SCHEDULED = "scheduled";
    private static final String KEY_EXPORTABLE = "exportable";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"));

    @Container
    private static final PostgreSQLContainer POSTGRE_SQL_CONTAINER = (PostgreSQLContainer) new PostgreSQLContainer("postgres:13.5-alpine")
            .withUsername(PG_USERNAME)
            .withPassword(PG_PASSWORD)
            .withInitScript("volume.sql");

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTimestampsTopicName;

    @Value("${kafka.topics.execution-report.topic-name}")
    private String executionReportTopicName;

    @Value("${kafka.topics.scheduled.topic-name}")
    private String scheduledTopicName;

    @Value("${kafka.topics.on-demand.topic-name}")
    private String onDemandTopicName;

    @Autowired
    private Status status;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mvc;
    private Map<String, String> initialMetrics;

    @DynamicPropertySource
    static void dynamicKafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("datasource.enabled", () -> true);
        registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", () -> PG_USERNAME);
        registry.add("spring.datasource.driver-class-name", POSTGRE_SQL_CONTAINER::getDriverClassName);
        registry.add("spring.datasource.password", () -> PG_PASSWORD);
        registry.add("spring.datasource.hikari.connectionTimeout", () -> 250);
    }

    @BeforeAll
    static void setup() {
        executionReportKafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(
                new HashMap<>(KafkaTestUtils.producerProps(KAFKA_CONTAINER.getBootstrapServers())),
                new StringSerializer(), new JsonSerializer<>()));
        scheduledTopicMessageListenerContainer = createAvroConsumer(SCHEDULED_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), true);
        scheduledTopicConsumer = (Consumer) scheduledTopicMessageListenerContainer.getContainerProperties().getMessageListener();
        onDemandTopicMessageListenerContainer = createAvroConsumer(ON_DEMAND_TOPIC_NAME, KAFKA_CONTAINER.getBootstrapServers(), true);
        onDemandTopicConsumer = (Consumer) onDemandTopicMessageListenerContainer.getContainerProperties().getMessageListener();
        OBJECT_MAPPER.configure(Feature.ALLOW_COMMENTS, true);
    }

    @AfterAll
    static void teardown() {
        scheduledTopicMessageListenerContainer.stop();
        executionReportKafkaTemplate.getProducerFactory().reset();
    }

    @BeforeEach
    @SneakyThrows
    void resetStatus() {
        kafkaListenerEndpointRegistry.getListenerContainer(EXECUTION_REPORT_LISTENER_ID).start();
        BeanUtils.copyProperties(new Status(), status);
        sendExecutionReport("0");
        scheduledTopicConsumer.flush();
        await().during(1, TimeUnit.SECONDS).atMost(2, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void whenApplicationStarts_shouldTopicsBeCreated() {
        try (AdminClient adminClient = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_CONTAINER.getBootstrapServers()))) {
            assertThat(adminClient.listTopics()
                    .names()
                    .get())
                    .contains(backupTopicName)
                    .contains(completedTimestampsTopicName)
                    .contains(executionReportTopicName)
                    .contains(scheduledTopicName)
                    .contains(onDemandTopicName);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("InterruptedException in whenApplicationStarts_shouldTopicBeCreated", e);
        } catch (final ExecutionException e) {
            fail("ExecutionException in whenApplicationStarts_shouldTopicBeCreated", e);
        }
    }

    @Test
    void whenReliabilityThresholdNotUpdated_shouldNothingBeExported() {
        sendExecutionReport("1");
        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
    }

    @Test
    void whenReliabilityThresholdUpdatedForNonExportableKpis_shouldNothingBeExported() {
        sendExecutionReport("2");
        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
    }

    @Test
    void whenCommonReliabilityUpdatedWithOneAggPerForOneTable_shouldNecessaryRowsBeExported() {
        sendExecutionReport("3");
        assertWithJsonData("3");
    }

    @Test
    void whenCommonReliabilityUpdatedWithOneAggPerForMultipleTables_shouldNecessaryRowsBeExported()
            throws InterruptedException {
        sendExecutionReport("4");
        assertWithJsonData("4");
    }

    @Test
    void whenCommonReliabilityUpdatedWithOneAggPerForAllTables_shouldNecessaryRowsBeExported() {
        sendExecutionReport("12");
        assertWithJsonData("12");
    }

    @Test
    void whenCommonReliabilityUpdatedWithMultipleAggPerForOneTable_shouldNecessaryRowsBeExported() {
        sendExecutionReport("5_setup");
        assertWithJsonData("5_A");
        sendExecutionReport("5_test");
        assertWithJsonData("5_B");
    }

    @Test
    void whenOnDemandExecutionReportReceived_shouldRowsFromSmallestCalculationStartTimeToGreatestReliabilityThresholdBeExported() {
        sendExecutionReport("6");
        assertWithJsonData("6", onDemandTopicConsumer);
    }

    @Test
    @SneakyThrows
    void whenLateDataReceivedForKpisWithLateDataFalse_shouldNothingBeExported() {
        sendExecutionReport(COMMON_USE_CASE_A);
        scheduledTopicConsumer.flush();
        sendExecutionReport("7_test");
        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
    }

    @Test
    @SneakyThrows
    void whenLateDataReceivedForKpisWithLateDataTrue_shouldNecessaryRowsBeExported() {
        sendExecutionReport(COMMON_USE_CASE_A);
        scheduledTopicConsumer.flush();
        sendExecutionReport("8_test");
        assertWithJsonData("8");
    }

    @Test
    void whenLateDataReceivedForKpisWithLateDataTrueButAlsoReliabilityIncreased_shouldNecessaryRowsBeExportedAndReexported() {
        sendExecutionReport(COMMON_USE_CASE_A);
        assertWithJsonData("9_A");
        sendExecutionReport("9_test");
        assertWithJsonData("9_B");
    }

    @Test
    @SneakyThrows
    void whenLateDataReceivedForNonExportableKpisWithLateDataTrue_shouldNothingBeExported() {
        sendExecutionReport(COMMON_USE_CASE_A);
        scheduledTopicConsumer.flush();
        sendExecutionReport("10_test");
        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
    }

    @Test
    @SneakyThrows
    void whenLateDataReceivedForKpisWithLateDataTrueAndWithLateDataFalse_shouldNecessaryRowsBeExported() {
        sendExecutionReport("11_setup");
        scheduledTopicConsumer.flush();
        sendExecutionReport("11_test");
        assertWithJsonData("11");
    }

    @Test
    void whenKpisAdded_shouldNecessaryKpisAppear() {
        sendExecutionReport("13");
        assertWithJsonData("13");
    }

    @Test
    void whenKpisDeleted_shouldNecessaryKpisDisappear() {
        sendExecutionReport("14");
        assertWithJsonData("14");
    }

    @Test
    @SneakyThrows
    void whenScheduledExecutionReportReceived_shouldScheduledCounterIncreaseAndOnDemandNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("1");
        sendExecutionReport("1");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String> modifiedMetrics = retrieveMetrics();

        String metricName = String.format(
                NUMBER_OF_REPORTS_FORMAT, TRUE);
        assertThat(modifiedMetrics)
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 2));

        metricName = String.format(
                NUMBER_OF_REPORTS_FORMAT, FALSE);
        final String onDemandExecutionReportCounter = modifiedMetrics.get(metricName) == null
                ? METRIC_NULL_NULL : modifiedMetrics.get(metricName);
        assertThat(onDemandExecutionReportCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, metricName, 0));
    }

    @Test
    @SneakyThrows
    void whenOnDemandExecutionReportReceived_shouldOnDemandCounterIncreaseAndScheduledNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("6");
        sendExecutionReport("6");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));

        final String scheduledMetricName = String.format(
                NUMBER_OF_REPORTS_FORMAT, TRUE);
        final String onDemandMetricName = String.format(
                NUMBER_OF_REPORTS_FORMAT, FALSE);
        assertThat(retrieveMetrics())
                .containsEntry(scheduledMetricName, expectedMetricsValue(initialMetrics, scheduledMetricName, 0))
                .containsEntry(onDemandMetricName, expectedMetricsValue(initialMetrics, onDemandMetricName, 2));
    }

    @Test
    @Order(1)
    @SneakyThrows
    void whenDifferentTablesInScheduledExecutionReportsReceived_shouldDifferentTableInScheduledReportsCounterIncreaseAndOnDemandNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        sendExecutionReport("3A");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String>  modifiedMetrics = retrieveMetrics();

        String metricName = String.format(
                NUMBER_OF_DIFFERENT_TABLES_FORMAT, TRUE);
        assertThat(modifiedMetrics)
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 2));

        metricName = String.format(
                NUMBER_OF_DIFFERENT_TABLES_FORMAT, FALSE);
        final String differentOnDemandTablesCounter = (modifiedMetrics.get(metricName) == null)
                ? METRIC_NULL_NULL : modifiedMetrics.get(metricName);
        assertThat(differentOnDemandTablesCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, metricName, 0));
    }

    @Test
    @Order(0)
    @SneakyThrows
    void whenDifferentTablesInOnDemandExecutionReportsReceived_shouldDifferentTableInOnDemandReportsCounterIncreaseAndScheduledNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("6A");
        sendExecutionReport("6B");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String>  modifiedMetrics = retrieveMetrics();

        String metricName = String.format(
                NUMBER_OF_DIFFERENT_TABLES_FORMAT, TRUE);
        assertThat(modifiedMetrics)
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 0));

        metricName = String.format(
                NUMBER_OF_DIFFERENT_TABLES_FORMAT, FALSE);
        final String differentOnDemandTablesCounter = (modifiedMetrics.get(metricName) == null)
                ? METRIC_NULL_NULL : modifiedMetrics.get(metricName);
        assertThat(differentOnDemandTablesCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, metricName, 2));
    }

    @Test
    @SneakyThrows
    void whenScheduledAndOnDemandExecutionReportsReceived_shouldNumberOfTablesReceivedCounterIncrease() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        sendExecutionReport("3");
        sendExecutionReport("3A");
        sendExecutionReport("6");
        sendExecutionReport("6");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));

        final String metricName = NUMBER_OF_TABLES_FORMAT;
        assertThat(retrieveMetrics())
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 10));
    }

    @Test
    @SneakyThrows
    void whenScheduledExecutionReportsWithDifferentAggregationPeriodTablesReceived_shouldScheduledTableOccurrenceCountersIncreaseAndOnDemandNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        sendExecutionReport("3");
        sendExecutionReport("3A");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String>  modifiedMetrics = retrieveMetrics();

        final String cell60ScheduledMetricName = String.format(
                OCCURRENCE_OF_TABLE_FORMAT, TRUE, KPI_CELL_SECTOR_60);
        final String cell1440ScheduledMetricName = String.format(
                OCCURRENCE_OF_TABLE_FORMAT, TRUE, KPI_CELL_SECTOR_1440);
        final String cell60OnDemandMetricName = String.format(
                OCCURRENCE_OF_TABLE_FORMAT, FALSE, KPI_CELL_GUID_60);

        assertThat(modifiedMetrics)
                .containsEntry(cell60ScheduledMetricName,
                        expectedMetricsValue(initialMetrics, cell60ScheduledMetricName, 2))
                .containsEntry(cell1440ScheduledMetricName,
                        expectedMetricsValue(initialMetrics, cell1440ScheduledMetricName, 2));

        final String onDemandTableOccurrenceCounter = modifiedMetrics.get(cell60OnDemandMetricName) == null
                ? METRIC_NULL_NULL : modifiedMetrics.get(cell60OnDemandMetricName);
        assertThat(onDemandTableOccurrenceCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, cell60OnDemandMetricName, 0));
    }

    @Test
    @SneakyThrows
    void whenOnDemandExecutionReportsReceived_shouldOnDemandTableOccurrenceCountersIncreaseAndScheduledNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("6");
        sendExecutionReport("6");
        onDemandTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));

        final String cell60ScheduledMetricName = String.format(
                OCCURRENCE_OF_TABLE_FORMAT, TRUE, KPI_CELL_SECTOR_60);
        final String cell60OnDemandMetricName = String.format(
                OCCURRENCE_OF_TABLE_FORMAT, FALSE, KPI_CELL_GUID_60);

        assertThat(retrieveMetrics())
                .containsEntry(cell60OnDemandMetricName, expectedMetricsValue(initialMetrics,
                        cell60OnDemandMetricName, 2))
                .containsEntry(cell60ScheduledMetricName, expectedMetricsValue(initialMetrics,
                        cell60ScheduledMetricName, 0));
    }

    @Test
    @SneakyThrows
    void whenScheduledAndOnDemandExecutionReportsReceived_shouldNumberOfKpisCounterIncrease() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        sendExecutionReport("6");
        scheduledTopicConsumer.flush();
        onDemandTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));

        final String metricName = NUMBER_OF_KPIS_FORMAT;
        assertThat(retrieveMetrics())
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 22));
    }

    @Test
    @SneakyThrows
    void whenScheduledExecutionReportsAreProcessed_shouldNumberOfRowsReadFromPostgresCounterIncrease() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));

        assertThat(retrieveMetrics())
                .containsEntry("pm_stats_exporter_number_of_read_rows_from_postgres_total",
                        expectedMetricsValue(initialMetrics, "pm_stats_exporter_number_of_read_rows_from_postgres_total",
                                27));
    }

    @Test
    @SneakyThrows
    void whenScheduledExecutionReportsAreProcessed_shouldNumberOfTablesPutOnScheduledTopicCounterIncreaseAndOnDemandNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("3");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String>  modifiedMetrics = retrieveMetrics();

        String metricName = String.format(NUMBER_OF_RECORDS_PUT_FORMAT, scheduledTopicName);
        assertThat(modifiedMetrics)
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 27));

        metricName = String.format(NUMBER_OF_RECORDS_PUT_FORMAT, onDemandTopicName);
        final String onDemandRecordsCounter = modifiedMetrics.get(metricName) == null
                ? METRIC_NULL_NULL : modifiedMetrics.get(metricName);
        assertThat(onDemandRecordsCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, metricName, 0));
    }

    @Test
    @SneakyThrows
    void whenOnDemandExecutionReportsAreProcessed_shouldNumberOfRecordsPutOnOnDemandTopicCounterIncreasedAndScheduledNot() {
        initialMetrics = retrieveMetrics();

        sendExecutionReport("6");
        sendExecutionReport("6");
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String>  modifiedMetrics = retrieveMetrics();

        String metricName = String.format(NUMBER_OF_RECORDS_PUT_FORMAT, onDemandTopicName);
        assertThat(modifiedMetrics)
                .containsEntry(metricName, expectedMetricsValue(initialMetrics, metricName, 216));

        metricName = String.format(NUMBER_OF_RECORDS_PUT_FORMAT, scheduledTopicName);
        final String scheduledRecordsCounter = modifiedMetrics.get(metricName) == null
                ? METRIC_NULL_NULL : modifiedMetrics.get(metricName);
        assertThat(scheduledRecordsCounter)
                .isEqualTo(expectedMetricsValue(initialMetrics, metricName, 0));
    }

    @Test
    @SneakyThrows
    void whenExecutionReportsAreProcessed_shouldAllTimestampMetricsGetCollected() {
        final long dttm = System.currentTimeMillis();
        final String scheduledCaseId = "14";
        final List<String> scheduledExportableTestKpis = List.of("sum_integer_join1", "sum_float_join1");
        final List<String> scheduledNonExportableTestKpis = List.of("sum_integer_join2", "sum_integer_join3", "sum_float_join3");

        final String onDemandCaseId = "6";
        final List<String> onDemandExportableTestKpis = List.of("sum_integer_join1", "sum_float_join1", "sum_integer_join2", "sum_float_join2");
        final List<String> onDemandNonExportableTestKpis = List.of("sum_integer_join3", "sum_float_join3");

        sendExecutionReport(scheduledCaseId);
        sendExecutionReport(onDemandCaseId);
        scheduledTopicConsumer.flush();

        await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        final Map<String, String> modifiedMetrics = retrieveMetrics();

        assertAll("CheckScheduledExportableExecMetrics", () -> scheduledExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, EXECUTION_REPORT_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_SECTOR_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, SCHEDULED.name(),
                                KEY_EXPORTABLE, TRUE),
                        dttm)).isEqualTo(1L)
        ));

        assertAll("CheckScheduledNonExportableExecMetrics", () -> scheduledNonExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, EXECUTION_REPORT_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_SECTOR_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, SCHEDULED.name(),
                                KEY_EXPORTABLE, FALSE),
                        dttm)).isEqualTo(1L)
        ));

        assertAll("CheckOnDemandExportableExecMetrics", () -> onDemandExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, EXECUTION_REPORT_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_GUID_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, ON_DEMAND.name(),
                                KEY_EXPORTABLE, TRUE),
                        dttm)).isEqualTo(1L)
        ));

        assertAll("CheckOnDemandNonExportableExecMetrics", () -> onDemandNonExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, EXECUTION_REPORT_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_GUID_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, ON_DEMAND.name(),
                                KEY_EXPORTABLE, FALSE),
                        dttm)).isEqualTo(1L)
        ));

        assertAll("CheckScheduledAvroMetrics", () -> scheduledExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, COMPLETED_TIMESTAMP_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_SECTOR_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, SCHEDULED.name()),
                        dttm)).isGreaterThanOrEqualTo(1L)
        ));

        assertAll("CheckOnDemandAvroMetrics", () -> onDemandExportableTestKpis.forEach(
                kpi -> assertThat(isExpectedMultiGaugeTagValue(modifiedMetrics, COMPLETED_TIMESTAMP_COLLECTOR_METRIC_ID,
                        Map.of(KEY_ID, EXECUTION_ID,
                                KEY_TABLE, KPI_CELL_GUID_60,
                                KEY_KPI, kpi,
                                KEY_SCHEDULED, ON_DEMAND.name()),
                        dttm)).isGreaterThanOrEqualTo(1L)
        ));
    }

    @Test
    void logsContainCorrectTraceIds(final CapturedOutput output) {
        sendExecutionReport("3");
        assertWithJsonData("3");
        final Pattern pattern = Pattern.compile("trace_id\":\"([^\"]{32})");
        final String logs = output.getOut();
        final String [] lines = logs.split("\n");
        String ctsTopicWritingTraceId = "A";
        String scheduledTopicWritingTraceId = "B";

        for (final String line : lines) {
            if (line.contains(String.format("Finished writing message to '%s' topic", CTS_TOPIC_NAME))) {
                final Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    ctsTopicWritingTraceId = matcher.group();
                }
            }
            if (line.contains(String.format("Finished writing messages to '%s' topic", SCHEDULED_TOPIC_NAME))) {
                final Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    scheduledTopicWritingTraceId = matcher.group();
                }
            }
        }
        assertThat(ctsTopicWritingTraceId).isEqualTo(scheduledTopicWritingTraceId);
    }

    /**
     * Helper method to send execution reports from the path defined in the string literal to Kafka.
     *
     * @param useCase Use case part of the file e.g: use_case_0.json. From this file name the number is extracted.
     */
    @SneakyThrows
    private void sendExecutionReport(final String useCase) {
        executionReportKafkaTemplate.send(executionReportTopicName, OBJECT_MAPPER
                .readValue(KafkaToKafkaE2ETestIT.class.getResourceAsStream(
                        String.format("/execution_reports/use_case_%s.json", useCase)), ExecutionReport.class));
    }

    /**
     * Helper method to assert that the json data defined in the use case files is equal to the messages defined in the topic.
     *
     * @param useCase Use case part of the file e.g: use_case_0.json. From this file name the number is extracted.
     */
    private void assertWithJsonData(final String useCase) {
        assertWithJsonData(useCase, scheduledTopicConsumer);
    }

    /**
     * Helper method to assert that the json data defined in the use case files is equal to the messages defined in the topic.
     *
     * @param useCase Use case part of the file e.g: use_case_0.json. From this file name the number is extracted.
     * @param consumer which topic consumer to be used.
     */
    private void assertWithJsonData(final String useCase, final Consumer consumer) {
        try {
            final List<Map<String, Object>> data =
                    OBJECT_MAPPER.readValue(this.getClass().getResourceAsStream(
                        String.format("/expected_outputs/use_case_%s.json", useCase)), List.class);
            for (final Map<String, Object> row: data) {
                final GenericRecord message = (GenericRecord) consumer.getNext(5, TimeUnit.SECONDS);
                row.forEach((columnName, actualValue) -> {
                    assertThat(actualValue)
                            .hasToString(message.get(columnName).toString());
                });
            }
            await().during(TIMEOUT, TimeUnit.SECONDS).atMost(TIMEOUT + 1, TimeUnit.SECONDS).until(() ->
                    null == scheduledTopicConsumer.getNext(200, TimeUnit.MILLISECONDS));
        } catch (final IOException e) {
            fail("IOException during ExecutionReport Reading", e);
        } catch (final InterruptedException e) {
            fail("InterruptedException during JsonData assertion: ", e);
        }
    }

    /**
     * Helper method to gather the metrics data regarding the PM Stats Exporter.
     *
     * @return map containing the metrics data.
     */
    @SneakyThrows
    private Map<String, String> retrieveMetrics() {
        final MvcResult result = mvc.perform(get("/actuator/prometheus").contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
                .andReturn();
        final Map<String, String> metrics = new HashMap<>();
        result.getResponse()
                .getContentAsString()
                .lines()
                .forEach(
                        metric -> {
                            if (!metric.startsWith("#")) {
                                final String[] metricSplit = metric.split("\\s+");
                                metrics.put(metricSplit[0], metricSplit[1]);
                            }
                        });
        return metrics;
    }

    /**
     * Helper method to determine if the custom metrics data has been added to metrics data of the PM Stats Exporter.
     *
     * @param metrics metrics map containing the metrics data regarding the PM Stats Exporter.
     * @param metricName determines which metrics data should have been modified.
     * @param incrementValue how much the metrics data has been modified.
     * @return returns the increased metrics data of the given attribute.
     */
    private String expectedMetricsValue(final Map<String, String> metrics,
                                     final String metricName,
                                     final int incrementValue) {
        String originalValue = metrics.get(metricName);
        if (originalValue == null) {
            originalValue = METRIC_NULL_NULL;
        }

        return String.valueOf(Float.parseFloat(originalValue) + incrementValue);
    }

    @SneakyThrows
    private long isExpectedMultiGaugeTagValue(final Map<String, String> metrics, final String metricKey,
                                                 final Map<String, String> tags, final long timestampLowerBound) {
        final String textMarker = "\"";
        final String tagEqualsMarker = "=";
        final List<Predicate<String>> predicates = tags.entrySet().stream().map(
                c -> (Predicate<String>) str -> str.contains(String.format("%1$s%2$s%3$s%4$s%3$s", c.getKey(),
                        tagEqualsMarker, textMarker, c.getValue()))).collect(Collectors.toList());

        return metrics.entrySet().stream()
                .filter(c -> c.getKey().trim().startsWith(metricKey)
                        && predicates.stream().allMatch(p -> p.test(c.getKey()))
                        && Double.parseDouble(c.getValue()) >= timestampLowerBound
                ).count();
    }
}
