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

package com.ericsson.oss.air.pm.stats.exporter.writer;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.CreateSchemaCommand;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataCategory;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataProviderType;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataService;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataServiceInstance;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataSpace;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.DataType;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageSchemaResponse;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjects;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjects.MessageDataTopic;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjects.MessageStatusTopic;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.google.common.base.Stopwatch;

import reactor.core.publisher.Mono;

/**
 * Class containing REST calls for the DataCatalog.
 */
@Component
@ConditionalOnProperty("dc.enabled")
public class DataCatalogWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataCatalogWriter.class);

    @Autowired
    private WebClient webClient;

    @Autowired
    private int messageBusId;

    @Autowired
    @Qualifier("dataCatalogCache")
    private List<String> dataCatalogCache;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

    /**
     * Registers the topic and schemaId to the DataCatalog.
     *
     * @param topicName the name of the topic, were the message was sent.
     * @param schemaName Name of the SR schema
     * @param schemaVersion Version of the SR schema
     */
    public void createSchema(final String topicName, final String schemaName, final Integer schemaVersion) {
        if (!dataCatalogCache.contains(String.format("%s:%d", schemaName, schemaVersion))) {
            final CreateSchemaCommand command = buildCreateSchemaCommand(topicName, schemaName, schemaVersion);
            final String uri = "/catalog/v1/message-schema";
            LOGGER.debug("Sending PUT request to DataCatalog on uri: {}, with body: {}", uri, command);

            completedTimestampStopwatch.stop();
            final Optional<ResponseEntity<MessageSchemaResponse>> responseEntity = webClient.put()
                    .uri(uri)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(command), CreateSchemaCommand.class)
                    .retrieve()
                    .toEntity(MessageSchemaResponse.class)
                    .onErrorResume(WebClientResponseException.class, ex -> ex.getStatusCode().value() == 409 ? Mono.empty() : Mono.error(ex))
                    .blockOptional(Duration.ofSeconds(2));

            responseEntity.ifPresentOrElse(
                    response -> LOGGER.debug("PUT request to {} has returned with status code: {}, and body: {}",
                            uri, response.getStatusCode(), response.getBody()),
                    () -> LOGGER.debug("PUT request to {} has returned with status code: {}",
                            uri, HttpStatus.CONFLICT));
            final MessageSchemaResponse body =
                    responseEntity.map(messageSchemaResponseResponseEntity -> Objects.requireNonNull(messageSchemaResponseResponseEntity.getBody()))
                                  .orElseGet(() -> new MessageSchemaResponse(new DataType(schemaName, schemaVersion)));

            completedTimestampStopwatch.start();

            meterRegistryHelper.incrementCreatedSchemasCounter();
            final DataType dataType = body.getDataType();
            dataCatalogCache.add(String.format("%s:%d", dataType.getSchemaName(), dataType.getSchemaVersion()));
            LOGGER.debug("MessageSchema for schema {} with version {} was cached", dataType.getSchemaName(), dataType.getSchemaVersion());
        }
    }

    private CreateSchemaCommand buildCreateSchemaCommand(final String topicName, final String schemaName, final Integer schemaVersion) {
        return new CreateSchemaCommand(
                DataSpace.build(),
                DataProviderType.build(),
                MessageStatusTopic.build(messageBusId, topicName, schemaName),
                MessageDataTopic.build(messageBusId, topicName),
                DataType.build(schemaName, schemaVersion),
                DataCategory.build(),
                new SchemaObjects.MessageSchema(schemaName),
                DataService.build(),
                DataServiceInstance.build(schemaName, schemaVersion)
        );
    }
}
