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

import java.util.List;

import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Wrapper class which contains the Schema specific objects for V2 GET request.
 */
@SuppressWarnings("PMD.MissingStaticMethodInNonInstantiatableClass")
@Generated
public final class SchemaObjectsV2 {
    private SchemaObjectsV2() {
    }

    /**
     * POJO class representing the MessageSchema from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class MessageSchemaDto {
        private int id;
        private DataServiceDto dataService;
        private MessageDataTopicDto messageDataTopic;
        @NonNull
        private DataTypeDto dataType;
        private String specificationReference;
    }

    /**
     * POJO class representing the DataService from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataServiceDto {
        private int id;
        private String dataServiceName;
        private List<DataServiceInstanceDto> dataServiceInstance;
        private List<PredicateParameterDto> predicateParameter;
    }

    /**
     * POJO class representing the MessageDataTopic from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class MessageDataTopicDto {
        private int id;
        private String name;
        private String encoding;
        private List<Integer> messageSchemaIds;
        private MessageBusDto messageBus;
        private DataProviderTypeDto dataProviderType;
        private MessageStatusTopicDto messageStatusTopic;
    }

    /**
     * POJO class representing the DataType from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class DataTypeDto {
        private int id;
        private String mediumType;
        private int mediumId;
        @NonNull
        private String schemaName;
        @NonNull
        private String schemaVersion;
        private Boolean isExternal;
        private String consumedDataSpace;
        private String consumedDataCategory;
        private String consumedDataProvider;
        private String consumedSchemaName;
        private String consumedSchemaVersion;
    }

    /**
     * POJO class representing the DataServiceInstance from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataServiceInstanceDto {
        private int id;
        private DataService dataService;
        private String dataServiceInstanceName;
        private String controlEndpoint;
        private String consumedDataSpace;
        private String consumedDataCategory;
        private String consumedDataProvider;
        private String consumedSchemaVersion;
        private String consumedSchemaName;
    }

    /**
     * POJO class representing the PredicateParameter from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class PredicateParameterDto {
        private int id;
        private String parameterName;
        private Boolean isPassedToConsumedService;
        private DataService dataService;
    }

    /**
     * POJO class representing the MessageBus from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class MessageBusDto {
        private int id;
        private String name;
        private String clusterName;
        private String nameSpace;
        private List<String> accessEndpoints;
        private List<Integer> notificationTopicIds;
        private List<Integer> messageStatusTopicIds;
        private List<Integer> messageDataTopicIds;
    }

    /**
     * POJO class representing the DataProviderType from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataProviderTypeDto {
        int id;
        private DataSpaceDto dataSpace;
        private DataCategoryTypeDto dataCategoryType;
        private List<Integer> notificationTopicIds;
        private List<Integer> messageDataTopicIds;
        private String providerVersion;
        private String providerTypeId;
    }

    /**
     * POJO class representing the MessageStatusTopic from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class MessageStatusTopicDto {
        private int id;
        private String name;
        private String specificationReference;
        private String encoding;
        private List<Integer> messageDataTopicIds;
        private MessageBusDto messageBus;
    }

    /**
     * POJO class representing the DataService from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataService {
        private int id;
        private String dataServiceName;
    }

    /**
     * POJO class representing the DataSpace from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataSpaceDto {
        private int id;
        private String name;
        private List<Integer> dataProviderTypeIds;
    }

    /**
     * POJO class representing the DataCategoryTypeDto from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    public static class DataCategoryTypeDto {
        private int id;
        private String dataCategoryName;
    }
}
