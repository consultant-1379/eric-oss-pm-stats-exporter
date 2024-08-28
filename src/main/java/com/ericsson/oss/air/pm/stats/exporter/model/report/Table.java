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

import java.util.List;

import org.hibernate.validator.constraints.UniqueElements;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO class representing the Table data in the ExecutionReport received on the kafka topic.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table {

    @JsonProperty("name")
    @NotEmpty(message = "The name field of Table must not be empty or null.")
    @Pattern(regexp = "kpi_(.)*(_((15)|(60)|(1440)))?", message = "Table name should be in the format: 'kpi_(.)*(_((15)|(60)|(1440)))?'")
    private String name;

    @JsonProperty("aggregation_period")
    @NotNull(message = "The aggregationPeriod of Table must not be null.")
    private Integer aggregationPeriod;

    @JsonProperty("list_of_dimensions")
    @NotEmpty(message = "The listOfDimensions field of Table must not be empty or null.")
    @UniqueElements(message = "The listOfDimensions field of Table must contain unique elements.")
    private List<@NotEmpty(message = "Dimension must not be null or empty.") String> listOfDimensions;

    @JsonProperty("list_of_kpis")
    @NotEmpty(message = "The listOfKpis field of Table must not be empty or null.")
    @UniqueElements(message = "The listOfKpis field of Table must contain unique elements.")
    private List<@NotEmpty(message = "Kpi ID must not be null or empty.") String> listOfKpis;

    @JsonProperty("kpis")
    @NotEmpty(message = "The kpis field of Table must not be empty or null.")
    @UniqueElements(message = "The kpis field of Table must contain unique elements.")
    private List<@NotNull(message = "Kpi must not be null.") @Valid ExecutionReportKpi> executionReportKpis;
}
