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

package com.ericsson.oss.air.pm.stats.exporter.writer;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.common.KafkaException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;

@SpringBootTest(classes = {KafkaWriter.class})
@ActiveProfiles("kafkaTest")
@TestInstance(Lifecycle.PER_CLASS)
public class KafkaWriterTest {
    private static final String TEST_TOPIC = "test-topic";
    private static final String TEST_EXCEPTION = "test exception";
    private static final String KEY = "key";
    private static final String MESSAGE = "test_message";
    private static final int LIST_SIZE = 2;
    private static List<GenericRecord> recordList = new ArrayList<>();

    @Autowired
    private KafkaWriter kafkaWriter;

    @MockBean
    private KafkaTemplate<String, GenericRecord> avroKafkaTemplate;

    @MockBean
    private KafkaTemplate<String, Object> jsonProducerTemplate;

    @MockBean
    private GenericRecord genericRecord;

    @MockBean
    private CompletableFuture<SendResult<String, Object>> future;

    @MockBean
    private CompletableFuture<SendResult<String, GenericRecord>> avroFuture;

    @MockBean
    private MeterRegistryHelper meterRegistryHelper;

    @MockBean
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

    @BeforeAll
    public void setup() {
        for (int i = 0; i < LIST_SIZE; i++) {
            recordList.add(genericRecord);
        }
    }

    @Test
    @SneakyThrows
    void whenWritingAvro_noExceptionIsThrownAndTemplateIsCalled() {
        when(avroKafkaTemplate.send(any(), any())).thenReturn(avroFuture);
        assertThatCode(() -> kafkaWriter.sendAvro(TEST_TOPIC, recordList)).doesNotThrowAnyException();
        verify(avroKafkaTemplate, times(LIST_SIZE)).send(TEST_TOPIC, genericRecord);
        verifyNoMoreInteractions(avroKafkaTemplate);
        verify(completedTimestampStopwatch, times(1)).start();
        verify(completedTimestampStopwatch, times(1)).stop();
        verify(meterRegistryHelper, times(LIST_SIZE)).incrementRecordsPutOnKafka(TEST_TOPIC);
    }

    @Test
    @SneakyThrows
    void whenWritingAvro_timeoutHappensAndRollbackIsInitiated() {
        when(avroFuture.get(1, TimeUnit.SECONDS)).thenThrow(new ExecutionException(TEST_EXCEPTION, new KafkaException(TEST_EXCEPTION)));
        when(avroKafkaTemplate.send(any(), any())).thenReturn(avroFuture);
        assertThatThrownBy(() -> kafkaWriter.sendAvro(TEST_TOPIC, recordList)).isInstanceOf(RollBackException.class);
        verify(avroKafkaTemplate, times(2)).send(TEST_TOPIC, genericRecord);
        verifyNoMoreInteractions(avroKafkaTemplate);
        verify(completedTimestampStopwatch, never()).start();
        verify(completedTimestampStopwatch, times(1)).stop();
        verify(meterRegistryHelper, never()).incrementRecordsPutOnKafka(TEST_TOPIC);
    }

    @Test
    @SneakyThrows
    void whenWritingAvro_interruptedExceptionIsThrownAndRollbackIsInitiated() {
        when(avroFuture.get(1, TimeUnit.SECONDS)).thenThrow(new InterruptedException());
        when(avroKafkaTemplate.send(any(), any())).thenReturn(avroFuture);
        assertThatThrownBy(() -> kafkaWriter.sendAvro(TEST_TOPIC, recordList)).isInstanceOf(RollBackException.class);
        verify(avroKafkaTemplate, times(2)).send(TEST_TOPIC, genericRecord);
        verifyNoMoreInteractions(avroKafkaTemplate);
        verify(completedTimestampStopwatch, never()).start();
        verify(completedTimestampStopwatch, times(1)).stop();
        verify(meterRegistryHelper, never()).incrementRecordsPutOnKafka(TEST_TOPIC);
    }

    @Test
    @SneakyThrows
    void whenWritingAvro_timeoutExceptionIsThrownAndRollbackIsInitiated() {
        when(avroFuture.get(1, TimeUnit.SECONDS)).thenThrow(new TimeoutException());
        when(avroKafkaTemplate.send(any(), any())).thenReturn(avroFuture);
        assertThatThrownBy(() -> kafkaWriter.sendAvro(TEST_TOPIC, recordList)).isInstanceOf(RollBackException.class);
        verify(avroKafkaTemplate, times(2)).send(TEST_TOPIC, genericRecord);
        verifyNoMoreInteractions(avroKafkaTemplate);
        verify(completedTimestampStopwatch, never()).start();
        verify(completedTimestampStopwatch, times(1)).stop();
        verify(meterRegistryHelper, never()).incrementRecordsPutOnKafka(TEST_TOPIC);
    }

    @Test
    @SneakyThrows
    void whenWritingJson_noExceptionIsThrownAndTemplateIsCalled() {
        when(jsonProducerTemplate.send(any(), any(), any())).thenReturn(future);
        assertThatCode(() -> kafkaWriter.sendJson(TEST_TOPIC, KEY, MESSAGE)).doesNotThrowAnyException();
        verify(jsonProducerTemplate, times(1)).send(TEST_TOPIC, KEY, MESSAGE);
        verifyNoMoreInteractions(jsonProducerTemplate);
    }

    @Test
    @SneakyThrows
    void whenWritingJson_timeoutHappensAndRollbackIsInitiated() {
        when(future.get(1, TimeUnit.MINUTES)).thenThrow(new TimeoutException(TEST_EXCEPTION));
        when(jsonProducerTemplate.send(any(), any(), any())).thenReturn(future);
        assertThatThrownBy(() -> kafkaWriter.sendJson(TEST_TOPIC, KEY, MESSAGE)).isInstanceOf(RollBackException.class);
        verify(jsonProducerTemplate, times(1)).send(TEST_TOPIC, KEY, MESSAGE);
        verifyNoMoreInteractions(jsonProducerTemplate);
    }

    @Test
    @SneakyThrows
    void whenWritingJson_interrupetEdxceptionIsThrownAndRollbackIsInitiated() {
        when(future.get(1, TimeUnit.MINUTES)).thenThrow(new InterruptedException(TEST_EXCEPTION));
        when(jsonProducerTemplate.send(any(), any(), any())).thenReturn(future);
        assertThatThrownBy(() -> kafkaWriter.sendJson(TEST_TOPIC, KEY, MESSAGE)).isInstanceOf(RollBackException.class);
        verify(jsonProducerTemplate, times(1)).send(TEST_TOPIC, KEY, MESSAGE);
        verifyNoMoreInteractions(jsonProducerTemplate);
    }

    @Test
    @SneakyThrows
    void whenWritingJson_executionExceptionIsThrownAndRollbackIsInitiated() {
        when(future.get(1, TimeUnit.MINUTES)).thenThrow(new ExecutionException(TEST_EXCEPTION, new KafkaException(TEST_EXCEPTION)));
        when(jsonProducerTemplate.send(any(), any(), any())).thenReturn(future);
        assertThatThrownBy(() -> kafkaWriter.sendJson(TEST_TOPIC, KEY, MESSAGE)).isInstanceOf(RollBackException.class);
        verify(jsonProducerTemplate, times(1)).send(TEST_TOPIC, KEY, MESSAGE);
        verifyNoMoreInteractions(jsonProducerTemplate);
    }

    @Test
    @SneakyThrows
    void whenWritingJson_someArgsAreNull() {
        assertThatThrownBy(() -> kafkaWriter.sendJson(null, null, null)).isInstanceOf(NullPointerException.class);
        verify(jsonProducerTemplate, never()).send(any(), any(), any());
    }

    @Test
    @SneakyThrows
    void whenWritingAvro_listIsEmptyThenNullPointerExceptionIsThrown() {
        assertThatThrownBy(() -> kafkaWriter.sendAvro(TEST_TOPIC, null)).isInstanceOf(NullPointerException.class);
        verify(avroKafkaTemplate, never()).send(any(), any(), any());
        verify(completedTimestampStopwatch, never()).start();
        verify(completedTimestampStopwatch, never()).stop();
        verify(meterRegistryHelper, never()).incrementRecordsPutOnKafka(any());
    }
}