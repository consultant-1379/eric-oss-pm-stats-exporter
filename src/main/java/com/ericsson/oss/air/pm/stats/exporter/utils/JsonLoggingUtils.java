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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import com.google.gson.GsonBuilder;

/**
 * Logging Util class.
 */
public final class JsonLoggingUtils {

    private JsonLoggingUtils() {
    }

    /**
     * Convert Object into Json object.
     *
     * @param objectToJson object to convert
     * @return converted Json object
     */
    public static String objectToJson(final Object objectToJson) {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(objectToJson);
    }
}
