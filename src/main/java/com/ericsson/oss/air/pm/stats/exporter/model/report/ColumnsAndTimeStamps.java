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

package com.ericsson.oss.air.pm.stats.exporter.model.report;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * POJO class representing the TimeStamps information to know what can be exported.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnsAndTimeStamps {

    private List<String> dimensions;
    private List<String> kpis;
    private ExecutionReport.Scheduling scheduled;

    @SerializedName("start_time")
    private String startTime;

    @SerializedName("end_time")
    private String endTime;

    @SerializedName("table_name")
    private String tableName;

    private String triggerExecutionId;

    /**
     * Retrieve all columns.
     * @return all the columns
     */
    @JsonIgnore
    public List<String> getColumns() {
        final List<String> columns = new ArrayList<>(dimensions);
        columns.addAll(kpis);
        return columns;
    }
}
