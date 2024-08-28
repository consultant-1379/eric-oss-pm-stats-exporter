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

package com.ericsson.oss.air.pm.stats.exporter.model.innerstate;

import java.util.Optional;

import com.ericsson.oss.air.pm.stats.exporter.model.api.Kpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO class representing the KPI data in the Status.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KpiState implements Kpi {

    @JsonProperty("exportable")
    private boolean exportable;

    @JsonProperty("calculation_start_time")
    private String calculationStartTime;

    @JsonProperty("reliability_threshold")
    private String reliabilityThreshold;

    /**
     * The of() method is used to obtain a Kpi instance from the given kpi.
     *
     * @param from {@link ExecutionReportKpi} the received kpi in the execution report
     * @return {@link KpiState} mapped to the format needed by the exporter
     */
    public static KpiState of(final ExecutionReportKpi from) {
        final KpiState kpiState = new KpiState();
        kpiState.exportable = from.isExportable();
        kpiState.calculationStartTime = Optional.ofNullable(from.getCalculationStartTime()).orElse(Long.MAX_VALUE).toString();
        kpiState.reliabilityThreshold = from.getReliabilityThreshold().toString();
        return kpiState;
    }

    @Override
    public boolean isReexportLateDataEnabled() {
        return false;
    }

    @Override
    public Long longCalculationStartTime() {
        return calculationStartTime == null ? null : Long.parseLong(calculationStartTime);
    }

    @Override
    public Long longReliabilityThreshold() {
        return reliabilityThreshold == null ? null : Long.parseLong(reliabilityThreshold);
    }
}
