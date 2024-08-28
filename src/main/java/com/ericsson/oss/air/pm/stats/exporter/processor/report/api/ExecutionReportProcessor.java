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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.api;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport.Scheduling;

/**
 * Interface responsible for processing execution report messages.
 */
public interface ExecutionReportProcessor {
    /**
     * Determines if the given scheduling is supported or not.
     * @param scheduling Scheduling to be checked.
     * @return True if the scheduling is supported, otherwise false.
     */
    boolean supports(Scheduling scheduling);

    /**
     * Processing the message and updating or creating a corresponding Map entry for it in the InnerState.
     * @param message The message received on the topic.
     */
    void processMessage(ExecutionReport message);
}
