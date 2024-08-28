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

import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


/**
 * POJO class representation of the JSON message, the service will receive on the kafka topic.
 */
@Data
public class ExecutionReport {

    @JsonProperty("execution_id")
    @NotEmpty(message = "The executionId field of ExecutionReport must not be null or empty.")
    @Pattern(regexp = "^[0-9A-Fa-f]{8}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{4}-[0-9A-Fa-f]{12}$",
            message = "The executionId must be in valid UUID format.")
    private String executionId;

    @NotNull(message = "The scheduled field of ExecutionReport must not be null.")
    private Scheduling scheduled;

    @JsonProperty("execution_start")
    @NotNull(message = "The executionStart field of ExecutionReport must not be null.")
    @Range(min = 3600L, max = 32503676399L,
            message = "The executionStart must be greater than or equal to 3600L and less than or equal to "
            + "32503676399L and must be in valid number format.")
    private Long executionStart;

    @JsonProperty("execution_end")
    @NotNull(message = "The executionEnd field of ExecutionReport must not be null.")
    @Range(min = 3600L, max = 32503676399L,
            message = "The executionEnd must be greater than or equal to 3600L and less than or equal to "
                    + "32503676399L and must be in valid number format.")
    private Long executionEnd;

    @JsonProperty("execution_group")
    @NotEmpty(message = "The executionGroup field of ExecutionReport must not be null or empty.")
    private String executionGroup;

    @JsonProperty("tables")
    @NotEmpty(message = "The tables field of ExecutionReport must not be null or empty.")
    @UniqueElements(message = "The tables field of ExecutionReport must contain unique elements.")
    private List<@NotNull(message = "Table must not be null.") @Valid Table> tables;

    /**
     * Return if the scheduled field is Scheduled.
     * @return true if it is scheduled, false otherwise
     */
    @JsonProperty("scheduled")
    public boolean isScheduledJson() {
        return scheduled == Scheduling.SCHEDULED;
    }

    /**
     * Set the scheduled field.
     * @param scheduled true in case of Scheduled, false for On Demand
     */
    @JsonProperty("scheduled")
    public void setScheduledJson(final boolean scheduled) {
        this.scheduled = scheduled ? Scheduling.SCHEDULED : Scheduling.ON_DEMAND;
    }

    /**
     * Possible values for scheduling.
     */
    public enum Scheduling {
        SCHEDULED,
        ON_DEMAND
    }
}

