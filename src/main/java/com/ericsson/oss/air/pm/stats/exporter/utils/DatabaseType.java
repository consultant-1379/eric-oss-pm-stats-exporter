/*******************************************************************************
 * COPYRIGHT Ericsson 2022
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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import java.util.Arrays;

/**
 * Enum class for Database Types.
 */
public enum DatabaseType {
    /**
     * Identifies the PostgreSQL type {@code BIGINT}.
     */
    LONG("int8"),
    /**
     * Identifies the PostgreSQL type {@code INTEGER}.
     */
    INT("int4"),
    /**
     * Identifies the PostgreSQL type {@code DOUBLE PRECISION}.
     */
    DOUBLE("float8"),
    /**
     * Identifies the PostgreSQL type {@code REAL}.
     */
    FLOAT("float4"),
    /**
     * Identifies the PostgreSQL type {@code BOOLEAN}.
     */
    BOOLEAN("bool"),
    /**
     * Identifies the PostgreSQL type {@code VARCHAR}.
     */
    VARCHAR("varchar"),
    /**
     * Identifies the PostgreSQL type {@code INTEGER[]}.
     */
    INT_ARRAY("_int4"),
    /**
     * Identifies the PostgreSQL type {@code BOOLEAN[]}.
     */
    BOOLEAN_ARRAY("_bool"),
    /**
     * Identifies the PostgreSQL type {@code DOUBLE PRECISION[]}.
     */
    DOUBLE_ARRAY("_float8");

    private final String value;

    DatabaseType(final String value) {
        this.value = value;
    }

    /**
     * Converts a string into an enum object.
     * @param value a string value which needs to be converted
     * @return a DatabaseType of the converted value
     * @throws IllegalArgumentException if the given value has no correct representation
     */
    public static DatabaseType fromString(final String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findAny().orElseThrow(() -> new IllegalArgumentException(String.format("%s is not a recognizable type", value)));
    }
}
