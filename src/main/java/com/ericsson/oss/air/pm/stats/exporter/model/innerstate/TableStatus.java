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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;
import com.ericsson.oss.air.pm.stats.exporter.processor.report.utils.CalculationUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO class representing the Status of each Table.
 */
@Data
@NoArgsConstructor
public class TableStatus {

    @JsonProperty("last_exported")
    private String lastExported;

    @JsonProperty("aggregation_period")
    private Integer aggregationPeriod;

    @JsonProperty("list_of_dimensions")
    private List<String> listOfDimensions;

    private Map<String, KpiState> kpis;

    /**
     * Constructor for TableStatus.
     * @param table ExecutionReport.Table, table from the execution report message
     */
    public TableStatus(final Table table) {
        this.aggregationPeriod = table.getAggregationPeriod();
        this.listOfDimensions = table.getListOfDimensions();
        this.kpis = generateKpiMap(table);
        lastExported = CalculationUtils.calculateStartTimeForGenericKpi(kpis.entrySet(), aggregationPeriod);
    }

    /**
     * Generating a Map with the name of the Kpi and the value of it if present.
     * @param table The table containing information about the Kpis in it.
     * @return Map< String, Status.Kpi> with all the Kpis received in the message
     *         listOfKpis field for the table.
     */
    private Map<String, KpiState> generateKpiMap(final Table table) {
        final Map<String, KpiState> kpisInExecutionReport = new HashMap<>();
        table.getListOfKpis().forEach(kpiName -> kpisInExecutionReport.put(kpiName, null));
        table.getExecutionReportKpis().forEach(kpi -> kpisInExecutionReport.put(kpi.getName(), KpiState.of(kpi)));
        return kpisInExecutionReport;
    }

    /**
     * This method gives back the columns of the table which needs to be imported by
     * the Postgres Reader.
     * @return A List of the name of the kpi columns with exportable true.
     */
    @JsonIgnore
    public List<String> getExportableKpiList() {
        final Map<String, KpiState> filtered = new HashMap<>(kpis);
        filtered.values().removeIf(k -> !k.isExportable());
        return new ArrayList<>(filtered.keySet());
    }

    /**
     * This method checks if the listOfKpis in the table has been changed. If new
     * field is present, or old one is removed it will create/remove it from the
     * kpis map.
     * @param kpiList the listOfKpis present in the table.
     */
    public void checkForKpiChange(final List<String> kpiList) {
        final List<String> kpisInStatus = new ArrayList<>(kpis.keySet());
        final List<String> plusKpi = new ArrayList<>(kpiList);
        plusKpi.removeAll(kpisInStatus);
        kpisInStatus.removeAll(kpiList);
        if (!plusKpi.isEmpty()) {
            for (final String kpi : plusKpi) {
                kpis.put(kpi, null);
            }
        }
        if (!kpisInStatus.isEmpty()) {
            for (final String kpi : kpisInStatus) {
                kpis.remove(kpi);
            }
        }
    }

    /**
     * The method iterates through the given list to update the kpis map with the new data.
     * @param calculated all the Kpis that got calculated.
     */
    public void updateKpis(final List<ExecutionReportKpi> calculated) {
        final List<ExecutionReportKpi> filteredCalculated = calculated.stream().filter(Objects::nonNull)
                .collect(Collectors.toList());

        filteredCalculated.forEach(k -> kpis.put(k.getName(), KpiState.of(k)));
    }

    /**
     * The method checks through the kpi map and adds all kpi name to a list if its
     * exportable, or if we don't have info from it.
     * @return a list of the kpis with value null or exportable set to true.
     */
    public List<String> kpiListNullOrExportable() {
        final List<String> kpiList = new ArrayList<>();
        for (final Map.Entry<String, KpiState> entry : kpis.entrySet()) {
            final KpiState kpiState = entry.getValue();
            if (kpiState == null || kpiState.isExportable()) {
                kpiList.add(entry.getKey());
            }
        }
        return kpiList;
    }
}
