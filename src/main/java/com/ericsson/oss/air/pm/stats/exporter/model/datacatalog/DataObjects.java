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

package com.ericsson.oss.air.pm.stats.exporter.model.datacatalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper class which contains the Data specific objects.
 */
public final class DataObjects {
    public static final String PM_KPIS = "PM_KPIS";

    private static final String PMSCH = "pm-stats-calc-handling";

    private DataObjects() {
    }

    /**
     * POJO class representing the DataSpace from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataSpace {
        private String name;

        /**
         * Gives back the same DataSpace Instance, which will be needed for every MessageSchema registration.
         * @return the created DataSpace
         */
        public static DataSpace build() {
            return new DataSpace(PM_KPIS);
        }
    }

    /**
     * POJO class representing the DataService from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataService {
        private String dataServiceName;

        /**
         * Gives back the same DataService Instance, which will be needed for every MessageSchema registration.
         * @return the created DataService
         */
        public static DataService build() {
            return new DataService(PMSCH);
        }
    }

    /**
     * POJO class representing the DataServiceInstance from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DataServiceInstance {
        private String dataServiceInstanceName;
        private String controlEndPoint;
        private String consumedDataSpace;
        private String consumedDataCategory;
        private String consumedDataProvider;
        private String consumedSchemaName;
        private int consumedSchemaVersion;

        /**
         * Gives back the same DataServiceInstance Instance, which will be needed for every MessageSchema registration.
         * @param consumedSchemaName Name of the SR schema
         * @param consumedSchemaVersion Version of the SR schema
         * @return the created DataServiceInstance
         */
        public static DataServiceInstance build(final String consumedSchemaName, final Integer consumedSchemaVersion) {
            return DataServiceInstance.builder()
                    .dataServiceInstanceName(PMSCH)
                    .controlEndPoint("")
                    .consumedDataSpace(PMSCH)
                    .consumedDataCategory(PM_KPIS)
                    .consumedDataProvider(PMSCH)
                    .consumedSchemaName(consumedSchemaName)
                    .consumedSchemaVersion(consumedSchemaVersion)
                    .build();
        }
    }

    /**
     * POJO class representing the DataCategory from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataCategory {
        private String dataCategoryName;

        /**
         * Gives back the same DataCategory Instance, which will be needed for every MessageSchema registration.
         * @return the created DataCategory
         */
        public static DataCategory build() {
            return new DataCategory(PM_KPIS);
        }
    }

    /**
     * POJO class representing the DataProviderType from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataProviderType {
        private String providerTypeId;
        private String providerVersion;

        /**
         * Gives back the same DataProviderType Instance, which will be needed for every MessageSchema registration.
         * @return the created DataProviderType
         */
        public static DataProviderType build() {
            return new DataProviderType(PMSCH, "v1");
        }
    }

    /**
     * POJO class representing the DataType from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @RequiredArgsConstructor
    public static class DataType {
        private String mediumType;
        @NonNull
        private String schemaName;
        @NonNull
        private int schemaVersion;
        private String consumedDataSpace;
        private String consumedDataCategory;
        private String consumedDataProvider;
        private String consumedSchemaName;
        private int consumedSchemaVersion;

        /**
         * Gives back the same DataType Instance, which will be needed for every MessageSchema registration.
         * @param schemaName Name of the SR schema
         * @param schemaVersion Version of the SR schema
         * @return the created DataType
         */
        public static DataType build(final String schemaName, final Integer schemaVersion) {
            return DataType.builder()
                    .mediumType("stream")
                    .schemaName(schemaName)
                    .schemaVersion(schemaVersion)
                    .consumedDataSpace(PM_KPIS)
                    .consumedDataCategory(PM_KPIS)
                    .consumedDataProvider(PMSCH)
                    .consumedSchemaName(schemaName)
                    .consumedSchemaVersion(schemaVersion)
                    .build();
        }
    }
}