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

package com.ericsson.oss.air.pm.stats.exporter.model.report;

import org.hibernate.validator.constraints.Range;

import com.ericsson.oss.air.pm.stats.exporter.model.api.Kpi;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO class representing the KPI data in the ExecutionReport received on the kafka topic.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionReportKpi implements Kpi {

    @JsonProperty("name")
    @NotEmpty(message = "The name field of Kpi must not be null or empty.")
    private String name;

    @JsonProperty("reexport_late_data")
    @NotNull(message = "The reexportLateData field of Kpi must not be null.")
    private boolean reexportLateDataEnabled;

    @JsonProperty("calculation_start_time")
    @NotNull(message = "The calculationStartTime field of Kpi must not be null.")
    @Range(min = 3600L, max = 32503676399L,
            message = "The calculationStartTime must be greater than or equal to 3600L and less than or equal to "
                + "32503676399L and must be in valid number format.")
    private Long calculationStartTime;

    @JsonProperty("exportable")
    @NotNull(message = "The exportable field of Kpi must not be null.")
    private boolean exportable;

    @JsonProperty("reliability_threshold")
    @NotNull(message = "The reliabilityThreshold field of Kpi must not be null.")
    @Range(min = 3600L, max = 32503676399L,
            message = "The reliabilityThreshold must be greater than or equal to 3600L and less than or equal to "
                + "32503676399L and must be in valid number format.")
    private Long reliabilityThreshold;

    @Override
    public Long longCalculationStartTime() {
        return calculationStartTime;
    }

    @Override
    public Long longReliabilityThreshold() {
        return reliabilityThreshold;
    }
}
