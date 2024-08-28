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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper class which contains the Create Schema objects.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSchemaCommand {
    private DataObjects.DataSpace dataSpace;
    private DataObjects.DataProviderType dataProviderType;
    private SchemaObjects.MessageStatusTopic messageStatusTopic;
    private SchemaObjects.MessageDataTopic messageDataTopic;
    private DataObjects.DataType dataType;
    private DataObjects.DataCategory dataCategory;
    private SchemaObjects.MessageSchema messageSchema;
    private DataObjects.DataService dataService;
    private DataObjects.DataServiceInstance dataServiceInstance;
}
