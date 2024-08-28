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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.annotation.DirtiesContext.ClassMode;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import com.ericsson.oss.air.pm.stats.exporter.helpers.config.TestBeans;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

@SpringBootTest(classes = {CustomMetrics.class},
        properties = {"meterCollectors.enabled = true", "meterCollectors.retentionSeconds = 2"})
@ContextConfiguration(classes = TestBeans.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class CustomMetricsTest {
    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private CustomMetrics customMetrics;

    private final String counter1Name = "counter1";
    private final String counter1Description = "Counter 1 description";
    private final String counter1Labels = "";
    private final String counter2Name = "counter2";
    private final String counter2Description = "Counter 2 description";
    private final String counter2LabelsA = "c2lA1=c2LabelAValue1,c2lA2=c2LabelAValue2";
    private final String counter2LabelsB = "c2lB1=c2LabelBValue1,c3lB2=c2LabelBValue2";
    private final long counter2BIncValue = 42L;
    private final String counter3Name = "counter3";
    private final String counter3Description = "Counter 3 description";
    private final String counter3Labels = "c3l1=c3LabelValue1,c3l2=c3LabelValue2";
    private final String counter3EmptyLabels = "";

    private final String gauge1Name = "gauge1";
    private final String gauge1Description = "Gauge 1 description";
    private final long gauge1Value = 142L;
    private final List<Pair<String, Long>> gauge1LabelValueList = List.of(
            Pair.of("", gauge1Value)
    );
    private final String gauge2Name = "gauge2";
    private final String gauge2Description = "Gauge 2 description";
    private final String gauge2Labels1 = "g2l1=g2Label1Value,g2l2=g2Label2Value1";
    private final long gauge2Value1 = 242L;
    private final String gauge2Labels2 = "g2l1=g2Label1Value,g2l2=g2Label2Value2";
    private final long gauge2Value2 = 243L;
    private final List<Pair<String, Long>> gauge2LabelValueList = List.of(
            Pair.of(gauge2Labels1, gauge2Value1),
            Pair.of(gauge2Labels2, gauge2Value2)
    );

    @BeforeEach
    void init() {
        meterRegistry.clear();
        customMetrics.setMeterRegistry(meterRegistry);

        customMetrics.incrementCounter(counter1Name, counter1Description, counter1Labels);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsA);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsB, counter2BIncValue);

        customMetrics.setGauges(gauge1Name, gauge1Description, gauge1LabelValueList);
        customMetrics.setGauges(gauge2Name, gauge2Description, gauge2LabelValueList);
    }

    @Test
    void whenMetricsAreUsedAfterInit_ShouldBeRegistered() {
        assertThat(isCounterRegistered(counter1Name, counter1Description, counter1Labels, 1L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsA, 1L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsB, counter2BIncValue)).isTrue();

        assertThat(isGaugeRegistered(gauge1Name, gauge1Description, "", gauge1Value)).isTrue();
        assertThat(isGaugeRegistered(gauge2Name, gauge2Description, gauge2Labels1, gauge2Value1)).isTrue();
        assertThat(isGaugeRegistered(gauge2Name, gauge2Description, gauge2Labels2, gauge2Value2)).isTrue();
    }

    @Test
    void whenCountersIncreasedWithValue_ShouldBeRegistered() {
        customMetrics.incrementCounter(counter1Name, counter1Description, counter1Labels, 21);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsA, 22);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsB, 23);

        assertThat(isCounterRegistered(counter1Name, counter1Description, counter1Labels, 22L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsA, 23L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsB,
                counter2BIncValue + 23L)).isTrue();
    }

    @Test
    void whenCountersIncreased_ShouldBeRegistered() {
        customMetrics.incrementCounter(counter1Name, counter1Description, counter1Labels);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsA);
        customMetrics.incrementCounter(counter2Name, counter2Description, counter2LabelsB);

        assertThat(isCounterRegistered(counter1Name, counter1Description, counter1Labels, 2L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsA, 2L)).isTrue();
        assertThat(isCounterRegistered(counter2Name, counter2Description, counter2LabelsB,
                counter2BIncValue + 1L)).isTrue();
    }

    @Test
    void whenGaugesSetAfterInit_ShouldBeRegistered() {
        final List<Pair<String, Long>> gauge1LabelValueListNew = List.of(
                Pair.of("", 1001L)
        );
        final List<Pair<String, Long>> gauge2LabelValueListNew = List.of(
                Pair.of(gauge2Labels1, 1002L),
                Pair.of(gauge2Labels2, 1003L)
        );

        customMetrics.setGauges(gauge1Name, gauge1Description, gauge1LabelValueListNew);
        customMetrics.setGauges(gauge2Name, gauge2Description, gauge2LabelValueListNew);

        assertThat(isGaugeRegistered(gauge1Name, gauge1Description, "", 1001L)).isTrue();
        assertThat(isGaugeRegistered(gauge2Name, gauge2Description, gauge2Labels1, 1002L)).isTrue();
        assertThat(isGaugeRegistered(gauge2Name, gauge2Description, gauge2Labels2, 1003L)).isTrue();
    }

    @Test
    void whenTimeIsOver_ShouldRemoveOldGauges() {
        final String gaugeNewName = "gauge_new";
        final String gaugeNewDescription = "Gauge new description";
        final String gaugeNewLabel = "gNewLabel=newLabelValue";
        final long gaugeNewValue = 542L;
        final List<Pair<String, Long>> gaugeNewLabelValueList = List.of(
                Pair.of(gaugeNewLabel, gaugeNewValue)
        );

        sleep2100millis();

        customMetrics.setGauges(gaugeNewName, gaugeNewDescription, gaugeNewLabelValueList);

        assertThat(meterRegistry.getMeters().stream()
                .filter(m -> m.getClass().getName().contains("Gauge"))
                .toList().size()).isEqualTo(1);
        final Gauge gauge = meterRegistry.get(gaugeNewName).tags(customMetrics.getTagListFromString(gaugeNewLabel)).gauge();
        assertThat(gauge.getId().getDescription()).isEqualTo(gaugeNewDescription);
        assertThat(gauge.value()).isEqualTo(gaugeNewValue);

        assertThat(customMetrics.getGaugeDataList().size()).isEqualTo(1);
        assertThat(customMetrics.getGaugeDataList().get(0).getName()).isEqualTo(gaugeNewName);
        assertThat(customMetrics.getGaugeDataList().get(0).getLabels()).isEqualTo(gaugeNewLabel);
        assertThat(customMetrics.getGaugeDataList().get(0).getValue()).isEqualTo(gaugeNewValue);
    }

    @Test
    void whenEmptyLabels_ShouldRegisterAndGiveBackCounter() {
        final Counter counter = customMetrics.initializeCounter(counter3Name, counter3Description, counter3EmptyLabels);

        assertThat(meterRegistry.get(counter3Name).counter().getId().getTags()).isEqualTo(Collections.emptyList());
        assertThat(counter.getId().getName()).isEqualTo(counter3Name);
        assertThat(counter.getId().getTags()).isEqualTo(Collections.emptyList());
    }

    @Test
    void whenWithLabels_ShouldRemoveWithEmptyLabelsAndRegisterWithLabelsAndGiveBackCounter() {
        final Counter emptyLabelCounter = customMetrics.initializeCounter(counter3Name, counter3Description, counter3EmptyLabels);

        assertThat(meterRegistry.get(counter3Name).counter().getId().getTags()).isEqualTo(Collections.emptyList());
        assertThat(emptyLabelCounter.getId().getName()).isEqualTo(counter3Name);
        assertThat(emptyLabelCounter.getId().getTags()).isEqualTo(Collections.emptyList());

        final Counter counterWithLabels = customMetrics.initializeCounter(counter3Name, counter3Description, counter3Labels);

        assertThat(meterRegistry.get(counter3Name).counter().getId().getTags())
                .isEqualTo(List.of(Tag.of("c3l1", "c3LabelValue1"), Tag.of("c3l2", "c3LabelValue2")));
        assertThat(counterWithLabels.getId().getName()).isEqualTo(counter3Name);
        assertThat(counterWithLabels.getId().getTags())
                .isEqualTo(List.of(Tag.of("c3l1", "c3LabelValue1"), Tag.of("c3l2", "c3LabelValue2")));
    }

    boolean isCounterRegistered(final String name, final String description, final String labels, final long value) {
        final Counter counter = meterRegistry.counter(name, customMetrics.getTagListFromString(labels));
        final Meter.Id id = counter.getId();
        return name.equals(id.getName())
                && description.equals(id.getDescription())
                && Stream.of(labels.split(",")).filter(Predicate.not(String::isEmpty)).sorted().toList().equals(
                        id.getTags().stream().map(tag -> String.format("%s=%s", tag.getKey(), tag.getValue())).sorted().toList())
                && counter.count() == value;
    }

    boolean isGaugeRegistered(final String name, final String description, final String labels, final long value) {
        final Gauge gauge = meterRegistry.get(name).tags(customMetrics.getTagListFromString(labels)).gauge();
        return description.equals(gauge.getId().getDescription())
                && gauge.value() == value;
    }

    void sleep2100millis() {
        final long waitTimeMillis = 2100;
        final long startTime = System.currentTimeMillis();
        await().atMost(waitTimeMillis + 500, TimeUnit.MILLISECONDS)
                .until(() -> System.currentTimeMillis() - startTime > waitTimeMillis);
    }
}
