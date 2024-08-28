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
import static org.mockito.Mockito.when;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest(classes = ReadinessHealthIndicator.class)
@ActiveProfiles("test")
public class ReadinessHealthIndicatorTest {

    @MockBean
    private CountDownLatch innerStateRestorationLatch;

    @Autowired
    private ReadinessHealthIndicator readinessHealthIndicator;

    @ParameterizedTest(name = "[{index}] kafkaEnabled: ''{0}'', latchCount: ''{1}'', expectedDetail: ''{2}'', expectedStatus: ''{3}''")
    @CsvSource(value = {"false,0,null,UP", "true,0,null,UP", "true,1,Waiting for dependant services to be available,DOWN"}, nullValues = {"null"})
    void readinessStateTestForAllPossibilities(final boolean kafkaEnabled,
                                               final long latchCount,
                                               final String expectedDetail,
                                               final String expectedStatus) {
        ReflectionTestUtils.setField(readinessHealthIndicator, "kafkaEnabled", kafkaEnabled);
        when(innerStateRestorationLatch.getCount()).thenReturn(latchCount);
        final Health actual = readinessHealthIndicator.health();
        assertThat(actual.getDetails().get("Kafka"))
                .isEqualTo(expectedDetail);
        assertThat(actual.getStatus().getCode())
                .isEqualTo(expectedStatus);
    }
}
