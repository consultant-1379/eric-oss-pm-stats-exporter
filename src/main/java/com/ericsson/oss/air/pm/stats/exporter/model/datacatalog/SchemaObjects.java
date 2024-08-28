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
 * Wrapper class which contains the Schema specific objects.
 */
public final class SchemaObjects {
    private SchemaObjects() {
    }

    /**
     * POJO class representing the MessageDataTopic from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDataTopic {
        private String encoding;
        private int messageBusId;
        private String name;

        /**
         * Gives back a MessageDataTopic Instance, which will be kafka and topic specific.
         * @param messageBusId the id of the kafka in the DataCatalog
         * @param name the name of the topic
         * @return the created MessageDataTopic
         */
        public static MessageDataTopic build(final int messageBusId, final String name) {
            return new MessageDataTopic("AVRO", messageBusId, name);
        }
    }

    /**
     * POJO class representing the MessageStatusTopic from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageStatusTopic {
        private String encoding;
        private String name;
        private int messageBusId;
        private String specificationReference;

        /**
         * Gives back a MessageStatusTopic Instance, which will be kafka and topic specific.
         * @param messageBusId the id of the kafka in the DataCatalog
         * @param name the name of the topic
         * @param schemaName Name of the SR schema
         * @return the created MessageStatusTopic
         */
        public static MessageStatusTopic build(final int messageBusId, final String name, final String schemaName) {
            return new MessageStatusTopic("AVRO", name, messageBusId, schemaName);
        }
    }

    /**
     * POJO class representing the MessageSchema from the DataCatalog.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageSchema {
        private String specificationReference;
    }
}
