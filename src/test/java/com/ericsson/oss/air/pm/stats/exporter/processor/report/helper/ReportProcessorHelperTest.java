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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.helper;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.ON_DEMAND;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClientException;

import com.ericsson.oss.air.pm.stats.exporter.config.sr.SchemaRegistryConfig;
import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestRetryConfigurations;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.TableStatus;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.writer.KafkaWriter;
import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;

@SpringBootTest(classes = ReportProcessorHelper.class)
@ActiveProfiles("kafkaTest")
@ExtendWith(OutputCaptureExtension.class)
@MockBeans({@MockBean(Status.class),
        @MockBean(name = "execution-report_stopwatch", classes = Stopwatch.class)})
@ContextConfiguration(classes = TestRetryConfigurations.class)
public class ReportProcessorHelperTest {

    @Value("${kafka.topics.backup.topic-name}")
    private String backupTopicName;

    @Value("${kafka.topics.completed-timestamp.topic-name}")
    private String completedTsTopicName;

    @MockBean
    private KafkaWriter kafkaWriter;

    @MockBean
    private CountDownLatch reportProcessorCountDownLatch;

    @Autowired
    private ReportProcessorHelper reportProcessorHelper;

    @MockBean
    private SchemaRegistryConfig schemaRegistry;

    @Test
    void whenStatusCreated_shouldStatusContainGoodValues() throws InterruptedException {
        final ExecutionReport executionReport = buildDefaultExecutionReport();
        final Table table = executionReport.getTables().get(0);
        final TableStatus tableStatus = reportProcessorHelper.createNewTableStatus(table);

        assertThat(tableStatus)
                .isNotNull();
        assertThat(table.getAggregationPeriod())
                .isEqualTo(tableStatus.getAggregationPeriod());
        assertThat(tableStatus.getListOfDimensions())
                .containsAll(table.getListOfDimensions());
        assertThat(table.getExecutionReportKpis().get(0).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI1).getReliabilityThreshold());
        assertThat(table.getExecutionReportKpis().get(1).getReliabilityThreshold())
                .hasToString(tableStatus.getKpis().get(KPI2).getReliabilityThreshold());
    }

    @Test
    void whenWriteTimeStampsCalledWithImpossibleArgs_shouldIgnore() {
        final ExecutionReport executionReport = buildDefaultExecutionReport();
        final Table table = executionReport.getTables().get(0);
        final ColumnsAndTimeStamps columnsAndTimeStamps =
                new ColumnsAndTimeStamps(table.getListOfDimensions(), table.getListOfKpis(), ON_DEMAND,
                        TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, table.getName(), EXECUTION_ID);
        final long impossibleAggregationPeriod = 65536;

        reportProcessorHelper.writeTimestamps(columnsAndTimeStamps, impossibleAggregationPeriod, table.getName());
        verify(kafkaWriter, never()).sendJson(anyString(), anyString(), any());
    }

    @Test
    void whenWriteTimeStampsCalled_shouldProceedWithCorrectValues() {
        final ExecutionReport executionReport = buildDefaultExecutionReport();
        final Table table = executionReport.getTables().get(0);
        final long aggregation = table.getAggregationPeriod();
        final ColumnsAndTimeStamps columnsAndTimeStamps =
                new ColumnsAndTimeStamps(table.getListOfDimensions(), table.getListOfKpis(), ON_DEMAND,
                        TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, table.getName(), EXECUTION_ID);

        reportProcessorHelper.writeTimestamps(columnsAndTimeStamps, aggregation, table.getName());
        verify(kafkaWriter, timeout(5000)).sendJson(completedTsTopicName, table.getName(), columnsAndTimeStamps);
    }

    @Test
    void whenWriteToBackupTopicCalled_shouldMessageBeSentToBackupTopic() {
        reportProcessorHelper.writeToBackupTopic(new HashMap<>());
        verify(kafkaWriter).sendJson(eq(backupTopicName), anyString(), any());
    }

    @Test
    @SneakyThrows
    void whenUpdateCompatibilityFails_shouldApplicationExit(final CapturedOutput capturedOutput) {
        doThrow(RestClientException.class).when(schemaRegistry).updateCompatibilityFull(anyString());

        try (MockedStatic<SpringApplication> springApplicationMockedStatic = mockStatic(SpringApplication.class)) {
            springApplicationMockedStatic.when(() ->
                    SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class))).thenReturn(-1);

            final ExecutionReport executionReport = buildDefaultExecutionReport();
            final Table table = executionReport.getTables().get(0);
            reportProcessorHelper.createNewTableStatus(table);

            springApplicationMockedStatic.verify(() -> SpringApplication.exit(any(ApplicationContext.class), any(ExitCodeGenerator.class)));
            assertThat(capturedOutput.getOut())
                    .contains("Schema Registry is unavailable and connection wasn't restored in time");
        }
    }
}