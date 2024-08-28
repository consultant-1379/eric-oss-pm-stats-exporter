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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * POJO class representing the Status of the InnerState.
 */
@Data
@NoArgsConstructor
public class Status {

    private Map<String, TableStatus> statusMap = new HashMap<>();

    /**
     * Getting the status of table.
     * @param tableName table to get
     * @return the status
     */
    public TableStatus get(final String tableName) {
        return statusMap.get(tableName);
    }

    /**
     * Check if it includes a specific table.
     * @param tableName to check if included
     * @return true if it is containing the table name
     */
    public boolean containsTable(final String tableName) {
        return statusMap.containsKey(tableName);
    }

    /**
     * Storing a new status.
     * @param tableName to store
     * @param tableStatus to store
     * @return the new status
     */
    public TableStatus put(@NonNull final String tableName, @NonNull final TableStatus tableStatus) {
        return statusMap.put(tableName, tableStatus);
    }

    /**
     * Retrieve the entry set.
     * @return set of entries
     */
    public Set<Map.Entry<String, TableStatus>> entrySet() {
        return statusMap.entrySet();
    }

    /**
     * Refresh the last exported field of exported entries.
     * @param exported entries to update
     */
    public void updateLastExported(final Map<String, String> exported) {
        exported.values().removeIf(Objects::isNull);

        for (final Map.Entry<String, String> entry : exported.entrySet()) {
            statusMap.get(entry.getKey()).setLastExported(entry.getValue());
        }
    }
}
