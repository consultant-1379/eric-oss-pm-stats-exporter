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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.api.ExecutionReportProcessor;

/**
 * Registry holding all the ExecutionReportProcessors together.
 */
@Component
public class ExecutionReportProcessorRegistry {
    @Autowired(required = false)
    private List<ExecutionReportProcessor> executionReportProcessors;

    /**
     * Returns the appropriate ExecutionReportProcessors, or throws an exception, if it's not available.
     * @param scheduling to select the appropriate processor
     * @return the appropriate processor for the scheduling
     */
    public ExecutionReportProcessor get(final Scheduling scheduling) {
        return Optional.ofNullable(executionReportProcessors)
            .orElse(Collections.emptyList())
            .stream()
            .filter(executionReportProcessor -> executionReportProcessor.supports(scheduling))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find processor for scheduling type: '%s'", scheduling)));
    }
}
