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

package com.ericsson.oss.air.pm.stats.exporter.processor.cts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.SerializationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestRetryConfigurations;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;
import com.ericsson.oss.air.pm.stats.exporter.reader.PostgresReader;
import com.ericsson.oss.air.pm.stats.exporter.utils.AvroSchema;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.ericsson.oss.air.pm.stats.exporter.writer.DataCatalogWriter;
import com.ericsson.oss.air.pm.stats.exporter.writer.KafkaWriter;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import lombok.SneakyThrows;

@ActiveProfiles("kafkaTest")
@SpringBootTest(classes = {CompletedTimestampsProcessor.class})
@ExtendWith(OutputCaptureExtension.class)
@MockBeans({@MockBean(DataCatalogWriter.class),
        @MockBean(MeterRegistryHelper.class),
        @MockBean(SchemaRegistryClient.class),
        @MockBean(CustomMetrics.class)})
@ContextConfiguration(classes = TestRetryConfigurations.class)
public class CompletedTimestampsProcessorTest {
    private static final String TIMESTAMP = "timestamp";
    private static final ExecutionReport.Scheduling SCHEDULED = ExecutionReport.Scheduling.SCHEDULED;
    private static final String KPI_CELL_SECTOR_60 = "kpi_cell_sector_60";
    private static final String DIM1 = "dim1";
    private static final String DIM2 = "dim2";
    private static final String KPI1 = "kpi1";
    private static final String KPI2 = "kpi2";
    private static final String KPI3 = "kpi3";
    private static final long TS_2023_02_01_14_00_00 = 1675260000L;
    private static final String TS_2023_02_01_14_00_00_S = "1675260000";
    private static final String TS_2023_02_01_15_00_00_S = "1675263600";
    private static final List<String> DIMENSIONS = List.of(DIM1, DIM2);
    private static final List<String> KPIS = List.of(KPI1, KPI2, KPI3);
    private static final List<Map<String, Object>> DATA_FULL = List.of(
            new HashMap<>(Map.of(DIM1, 4, DIM2, "dimValue1", TIMESTAMP, TS_2023_02_01_14_00_00, KPI1,
                    966608473, KPI2, 7818.5965, KPI3, "27476439")),
            new HashMap<>(Map.of(DIM1, 5, DIM2, "dimValue2", TIMESTAMP, TS_2023_02_01_14_00_00, KPI1,
                    1113764514, KPI2, 10106.97172, KPI3, "182377383")),
            new HashMap<>(Map.of(DIM1, 6, DIM2, "dimValue3", TIMESTAMP, TS_2023_02_01_14_00_00, KPI1,
                    369929604, KPI2, 33247.8488, KPI3, "827534987"))
    );
    private static final String EXECUTION_ID = "b3ea0e28-6eca-4003-9cb6-77c85c78288a";
    private static final ColumnsAndTimeStamps TEST_COLUMNS_AND_TIMESTAMPS = new ColumnsAndTimeStamps(
            DIMENSIONS, KPIS, SCHEDULED, TS_2023_02_01_14_00_00_S, TS_2023_02_01_15_00_00_S, KPI_CELL_SECTOR_60,
            EXECUTION_ID);

    @Value("${kafka.topics.scheduled.topic-name}")
    private String scheduledTopicName;

    @Autowired
    private CompletedTimestampsProcessor completedTimestampsProcessor;

    @MockBean
    private AvroSchema avroSchema;

    @MockBean
    private KafkaWriter kafkaWriter;

    @MockBean
    private PostgresReader postgresReader;

    @MockBean
    private SchemaRegistryClient schemaRegistryClientMock;

    @Captor
    private ArgumentCaptor<List<String>> columnNamesArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> outputTopicArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<GenericRecord>> outputDataArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> tableNameArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> timestampCaptor;

    @Test
    @SneakyThrows
    void whenProcessingRegularMessageAndNoExceptions_shouldPgReaderAvroWriterDataMatch() {
        final Optional<ParsedSchema> optionalParsedSchemaMock = mock(Optional.class);
        doReturn(DATA_FULL).when(postgresReader).readDb(anyString(), anyList(), anyString());
        doReturn(new HashMap<>()).when(postgresReader).getColumnTypes(anyString(), anyList());
        doReturn(SchemaBuilder.record(KPI_CELL_SECTOR_60).fields().endRecord()).when(avroSchema).buildAvroSchema(anyString(), anyMap(), anyMap());
        doReturn(DATA_FULL).when(avroSchema).fillRecordWithData(anyList(), any(Schema.class));
        doNothing().when(kafkaWriter).sendAvro(anyString(), anyList());
        doReturn(true).when(optionalParsedSchemaMock).isPresent();
        doReturn(optionalParsedSchemaMock).when(schemaRegistryClientMock).parseSchema(anyString(), anyString(), anyList());

        completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS);

        verify(postgresReader, timeout(5000))
                .readDb(tableNameArgumentCaptor.capture(), columnNamesArgumentCaptor.capture(), timestampCaptor.capture());
        verify(kafkaWriter, timeout(5000))
                .sendAvro(outputTopicArgumentCaptor.capture(), outputDataArgumentCaptor.capture());

        assertThat(tableNameArgumentCaptor.getValue())
                .isEqualTo(KPI_CELL_SECTOR_60);
        assertThat(columnNamesArgumentCaptor.getValue())
                .isEqualTo(TEST_COLUMNS_AND_TIMESTAMPS.getColumns());
        assertThat(timestampCaptor.getValue())
                .isEqualTo(TS_2023_02_01_14_00_00_S);
        assertThat(outputTopicArgumentCaptor.getValue())
                .isEqualTo(scheduledTopicName);
        assertThat(outputDataArgumentCaptor.getValue())
                .isEqualTo(DATA_FULL);
    }

    @Test
    @SneakyThrows
    void whenDcThrows_shouldExceptionBeCaughtAndErrorLogged(final CapturedOutput capturedOutput) {
        final Optional<ParsedSchema> optionalParsedSchemaMock = mock(Optional.class);
        doReturn(DATA_FULL).when(postgresReader).readDb(anyString(), anyList(), anyString());
        doReturn(new HashMap<>()).when(postgresReader).getColumnTypes(anyString(), anyList());
        doReturn(SchemaBuilder.record(KPI_CELL_SECTOR_60).fields().endRecord()).when(avroSchema).buildAvroSchema(anyString(), anyMap(), anyMap());
        doReturn(DATA_FULL).when(avroSchema).fillRecordWithData(anyList(), any(Schema.class));
        doNothing().when(kafkaWriter).sendAvro(anyString(), anyList());
        doReturn(true).when(optionalParsedSchemaMock).isPresent();
        doReturn(mock(ParsedSchema.class)).when(optionalParsedSchemaMock).get();
        doReturn(optionalParsedSchemaMock).when(schemaRegistryClientMock).parseSchema(anyString(), anyString(), anyList());
        doThrow(RestClientException.class).when(schemaRegistryClientMock).getVersion(anyString(), any(ParsedSchema.class));

        completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS);

        assertThat(capturedOutput.getOut())
                .contains("Could not connect to Schema Registry in order to get the schema ID for Data Catalog");
    }

    @Test
    @SneakyThrows
    void whenQueryResultIsEmpty_shouldHaltProcessing(final CapturedOutput capturedOutput) {
        final ColumnsAndTimeStamps emptyCts = new ColumnsAndTimeStamps(
                new ArrayList<String>(List.of()), new ArrayList<String>(List.of()), SCHEDULED,
                TS_2023_02_01_14_00_00_S, TS_2023_02_01_15_00_00_S, KPI_CELL_SECTOR_60, EXECUTION_ID
        );
        completedTimestampsProcessor.processCompletedTimestamps(emptyCts);
        verify(kafkaWriter, after(1000).never()).sendAvro(anyString(), anyList());
        assertThat(capturedOutput.getOut())
                .contains("The database returned with an empty result, therefore nothing was written on the topic:");
    }

    @Test
    @SneakyThrows
    void whenProcessingRegularMessageAndRollbackException_shouldCorrectErrorLogAppear() {
        doReturn(DATA_FULL).when(postgresReader).readDb(anyString(), anyList(), anyString());
        doReturn(new HashMap<>()).when(postgresReader).getColumnTypes(anyString(), anyList());
        doReturn(SchemaBuilder.record(KPI_CELL_SECTOR_60).fields().endRecord()).when(avroSchema).buildAvroSchema(anyString(), anyMap(), anyMap());
        doReturn(DATA_FULL).when(avroSchema).fillRecordWithData(anyList(), any(Schema.class));

        doThrow(RollBackException.class).when(kafkaWriter).sendAvro(anyString(), anyList());

        assertThatThrownBy(() -> completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS))
                .isInstanceOf(RollBackException.class)
                .hasMessage("Could not send message in time: ");

        doNothing().when(kafkaWriter).sendAvro(anyString(), anyList());
    }

    @Test
    @SneakyThrows
    void whenPostgresNotAvailable_shouldApplicationExit(final CapturedOutput capturedOutput) {
        try (MockedStatic<SpringApplication> springApplicationMockedStatic = mockStatic(SpringApplication.class)) {
            springApplicationMockedStatic.when(() ->
                    SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class))).thenReturn(-1);

            doThrow(CannotGetJdbcConnectionException.class).when(postgresReader).readDb(anyString(), anyList(), anyString());

            completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS);

            springApplicationMockedStatic.verify(() -> SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class)));
            assertThat(capturedOutput.getOut())
                    .contains("Database is unavailable and connection wasn't restored in time");
        }
    }

    @Test
    @SneakyThrows
    void whenPostgresNotAvailableDuringSchemaBuild_shouldApplicationExit(final CapturedOutput capturedOutput) {
        try (MockedStatic<SpringApplication> springApplicationMockedStatic = mockStatic(SpringApplication.class)) {
            springApplicationMockedStatic.when(() ->
                    SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class))).thenReturn(-1);

            doReturn(DATA_FULL).when(postgresReader).readDb(anyString(), anyList(), anyString());
            doThrow(CannotGetJdbcConnectionException.class).when(postgresReader).getColumnTypes(anyString(), eq(DIMENSIONS));

            completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS);

            springApplicationMockedStatic.verify(() -> SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class)));
            assertThat(capturedOutput.getOut())
                    .contains("Database is unavailable and connection wasn't restored in time");
        }
    }

    @Test
    @SneakyThrows
    void whenSchemaRegistryNotAvailable_shouldApplicationExit(final CapturedOutput capturedOutput) {
        try (MockedStatic<SpringApplication> springApplicationMockedStatic = mockStatic(SpringApplication.class)) {
            springApplicationMockedStatic.when(() ->
                    SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class))).thenReturn(-1);

            doReturn(Collections.singletonList(Collections.singletonMap("kpi", "kpi")))
                    .when(postgresReader).readDb(anyString(), anyList(), anyString());

            doThrow(SerializationException.class).when(kafkaWriter).sendAvro(anyString(), anyList());

            completedTimestampsProcessor.processCompletedTimestamps(TEST_COLUMNS_AND_TIMESTAMPS);

            springApplicationMockedStatic.verify(() -> SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class)));
            assertThat(capturedOutput.getOut())
                    .contains("Schema Registry is unavailable and connection wasn't restored in time");
        }
    }
}