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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.registry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.api.ExecutionReportProcessor;

@SpringBootTest(classes = ExecutionReportProcessorRegistry.class)
@ActiveProfiles("test")
public class ExecutionReportProcessorRegistryTest {

    @MockBean
    private ExecutionReportProcessor executionReportProcessorMock;

    @Autowired
    private ExecutionReportProcessorRegistry executionReportProcessorRegistry;

    @ParameterizedTest(name = "[{index}] scheduled: ''{0}''")
    @EnumSource(Scheduling.class)
    void whenRegistryCalled_shouldCorrectProcessorBeReturned(final Scheduling scheduling) {
        doReturn(true).when(executionReportProcessorMock).supports(scheduling);
        doReturn(false).when(executionReportProcessorMock).supports(negate(scheduling));
        assertThat(executionReportProcessorRegistry.get(scheduling))
                .isEqualTo(executionReportProcessorMock);
        assertThatThrownBy(() -> executionReportProcessorRegistry.get(negate(scheduling)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("Could not find processor for scheduling type: '%s'", negate(scheduling)));
    }

    private Scheduling negate(final Scheduling scheduling) {
        return scheduling.equals(Scheduling.SCHEDULED) ? Scheduling.ON_DEMAND : Scheduling.SCHEDULED;
    }
}
