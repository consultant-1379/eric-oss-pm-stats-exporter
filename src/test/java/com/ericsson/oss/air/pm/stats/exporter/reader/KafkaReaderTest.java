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

package com.ericsson.oss.air.pm.stats.exporter.reader;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DEFAULT_COLUMNS_AND_TIMESTAMPS;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.buildDefaultExecutionReport;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;
import com.ericsson.oss.air.pm.stats.exporter.processor.cts.CompletedTimestampsProcessor;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.api.ExecutionReportProcessor;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.registry.ExecutionReportProcessorRegistry;
import com.ericsson.oss.air.pm.stats.exporter.utils.ExecutionReportValidatorUtil;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.ericsson.oss.air.pm.stats.exporter.utils.exception.InvalidJsonMsgException;
import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;

@SpringBootTest(classes = {KafkaReader.class, ExecutionReportValidatorUtil.class})
@ActiveProfiles("kafkaTest")
@MockBeans({
    @MockBean(MeterRegistryHelper.class),
    @MockBean(value = Stopwatch.class, name = "execution-report_stopwatch"),
    @MockBean(value = Stopwatch.class, name = "completed-timestamp_stopwatch")})
public class KafkaReaderTest {
    private static final String TEST_TOPIC = "test-topic";
    private static final String KEY = "key";
    private static final String STRING = "string";

    private static final MockedStatic<Stopwatch> STOPWATCH_MOCKED_STATIC = mockStatic(Stopwatch.class);
    private static final Status NEW_STATUS_MOCK = mock(Status.class);

    private static final ConsumerRecord<String, ExecutionReport> DEFAULT_EXECUTION_REPORT_CONSUMER_RECORD =
            new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, buildDefaultExecutionReport());
    private static final ConsumerRecord<String, ColumnsAndTimeStamps> DEFAULT_COLUMNS_AND_TIMESTAMPS_CONSUMER_RECORD =
            new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, DEFAULT_COLUMNS_AND_TIMESTAMPS);
    private static final ConsumerRecord<String, Status> DEFAULT_STATUS_CONSUMER_RECORD =
            new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, NEW_STATUS_MOCK);

    @MockBean
    private ExecutionReportProcessorRegistry executionReportProcessorRegistryMock;

    @MockBean
    private CompletedTimestampsProcessor completedTimestampsProcessorMock;

    @MockBean
    private Status statusMock;

    @MockBean
    private ExecutionReportValidatorUtil executionReportValidatorUtilMock;

    @Autowired
    private KafkaReader kafkaReader;

    @BeforeAll
    static void setup() {
        STOPWATCH_MOCKED_STATIC.when(() -> Stopwatch.createUnstarted()).thenReturn(mock(Stopwatch.class));
    }

    @AfterAll
    static void teardown() {
        STOPWATCH_MOCKED_STATIC.close();
    }

    @Test
    void whenReaderReceivesRegularMessageOnExecutionReportTopic_shouldDependenciesBeCalledAccordingly() {
        final ExecutionReportProcessor executionReportProcessorMock = mock(ExecutionReportProcessor.class);
        doReturn(executionReportProcessorMock).when(executionReportProcessorRegistryMock).get(Scheduling.SCHEDULED);

        kafkaReader.receiveOnExecutionReportTopic(DEFAULT_EXECUTION_REPORT_CONSUMER_RECORD);

        verify(executionReportProcessorRegistryMock).get(Scheduling.SCHEDULED);
        verify(executionReportProcessorMock).processMessage(buildDefaultExecutionReport());
    }

    @Test
    void whenReaderReceivesStringOnExecutionReportTopic_shouldRegistryNotBeCalled() {
        kafkaReader.receiveOnExecutionReportTopic(new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, STRING));

        verify(executionReportProcessorRegistryMock, after(5000).never()).get(any(Scheduling.class));
    }

    @Test
    void whenReaderReceivesInvalidExecutionReportOnExecutionReportTopic_shouldRegistryNotBeCalled() throws InvalidJsonMsgException {
        doThrow(InvalidJsonMsgException.class).when(executionReportValidatorUtilMock).validateExecutionReportMessage(any(ExecutionReport.class));

        kafkaReader.receiveOnExecutionReportTopic(DEFAULT_EXECUTION_REPORT_CONSUMER_RECORD);

        verify(executionReportProcessorRegistryMock, after(500).never()).get(any(Scheduling.class));
    }

    @SneakyThrows
    @Test
    void whenReaderReceivesRegularMessageOnCompletedTimestampsTopic_shouldDependenciesBeCalled() {
        doReturn(true).when(completedTimestampsProcessorMock).processCompletedTimestamps(any(ColumnsAndTimeStamps.class));

        kafkaReader.receiveOnCompletedTimestampTopic(DEFAULT_COLUMNS_AND_TIMESTAMPS_CONSUMER_RECORD);

        verify(completedTimestampsProcessorMock).processCompletedTimestamps(DEFAULT_COLUMNS_AND_TIMESTAMPS);
    }

    @SneakyThrows
    @Test
    void whenReaderReceivesStringOnCompletedTimestampsTopic_shouldDependenciesNotBeCalled() {
        doReturn(true).when(completedTimestampsProcessorMock).processCompletedTimestamps(any(ColumnsAndTimeStamps.class));

        kafkaReader.receiveOnCompletedTimestampTopic(new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, STRING));

        verify(completedTimestampsProcessorMock, after(500).never()).processCompletedTimestamps(DEFAULT_COLUMNS_AND_TIMESTAMPS);
    }

    @Test
    void whenReaderReceivesRegularMessageOnBackupTopic_shouldStatusBeSet() {
        try (MockedStatic<BeanUtils> beanUtilsMockedStatic = mockStatic(BeanUtils.class)) {
            kafkaReader.readInnerStateFromKafka(DEFAULT_STATUS_CONSUMER_RECORD);

            beanUtilsMockedStatic.verify(() -> BeanUtils.copyProperties(NEW_STATUS_MOCK, statusMock));
        }
    }

    @Test
    void whenReaderReceivesInvalidMessageOnBackupTopic_shouldStatusNotBeSet() {
        try (MockedStatic<BeanUtils> beanUtilsMockedStatic = mockStatic(BeanUtils.class)) {
            kafkaReader.readInnerStateFromKafka(new ConsumerRecord<>(TEST_TOPIC, 0, 0, KEY, STRING));

            beanUtilsMockedStatic.verify(() -> BeanUtils.copyProperties(NEW_STATUS_MOCK, statusMock), after(500).never());
        }
    }
}
