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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * POJO class representing the MessageSchemaResponse from the DataCatalog.
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class MessageSchemaResponse {
    private Integer id;
    private DataObjects.DataSpace dataSpace;
    private DataObjects.DataService dataService;
    private DataObjects.DataProviderType dataProviderType;
    private SchemaObjects.MessageDataTopic messageDataTopic;
    @NonNull
    private DataObjects.DataType dataType;
    private String specificationReference;
}
