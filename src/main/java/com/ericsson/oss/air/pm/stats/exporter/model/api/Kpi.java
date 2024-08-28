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

package com.ericsson.oss.air.pm.stats.exporter.model.api;

/**
 * Class to hold together all the traits shared by the two Kpi class in the codebase.
 */
public interface Kpi {

    /**
     * Gives information about the Kpi's exportability.
     * @return boolean if the Kpi is exportable
     */
    boolean isExportable();

    /**
     * Gives information about the re-exportability of the Kpi.
     * @return {@link Boolean}, based on the Kpi's {@link Reexport} property
     */
    boolean isReexportLateDataEnabled();

    /**
     * Gives back the calculationStartTime property in {@link Long} format.
     * @return calculationStartTime property cast to {@link Long} if necessary
     */
    Long longCalculationStartTime();

    /**
     * Gives back the reliabilityThreshold property in {@link Long} format.
     * @return reliabilityThreshold property cast to {@link Long} if necessary
     */
    Long longReliabilityThreshold();

    /**
     * Takes the dynamic type, and keeps only the necessary traits to be of Kpi type.
     * @param kpi The Dynamic Kpi type
     * @return the same object, but without the knowledge of the dynamic type
     */
    static Kpi of(final Kpi kpi) {
        return kpi;
    }
}
