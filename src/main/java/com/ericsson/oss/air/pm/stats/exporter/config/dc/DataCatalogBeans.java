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

package com.ericsson.oss.air.pm.stats.exporter.config.dc;

import static com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.DataObjects.PM_KPIS;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.MessageBus;
import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.SchemaObjectsV2.MessageSchemaDto;

import reactor.core.publisher.Mono;

/**
 * Configuration class for the dc related Bean constructions.
 */
@Configuration
@ConditionalOnProperty("dc.enabled")
public class DataCatalogBeans {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataCatalogBeans.class);

    private static final String MESSAGE_BUS_NOT_FOUND_EXCEPTION = "MessageBus Not Found";
    private static final int MAX_BYTE_COUNT = 10 * 1000 * 1000; // Default max value for REST Spring applications (10 MB)

    @Value("${dc.url}")
    private String dataCatalogUrl;

    @Value("${dc.kafka-name}")
    private String kafkaName;

    @Value("${dc.name-space}")
    private String nameSpace;

    /**
     * Bean that creates the webclient necessary for data-catalog component.
     * @return {@link WebClient}: the created webclient.
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(dataCatalogUrl)
                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(MAX_BYTE_COUNT))
                .build();
    }

    /**
     * Bean to retrieve the registered message bus's id.
     * @param webClient using this client to get the ID.
     * @return {@link Integer}: ID of the found message bus
     */
    @Bean
    public Integer messageBusId(final WebClient webClient) {
        final String uri = String.format("/catalog/v1/message-bus?name=%s&nameSpace=%s", kafkaName, nameSpace);
        LOGGER.debug("Sending GET request to DataCatalog on uri: {}", uri);

        final ResponseEntity<List<MessageBus>> responseEntity = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<MessageBus>>() {})
                .onErrorResume(
                WebClientResponseException.class, ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
                .blockOptional(Duration.ofSeconds(10))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("GET request to %s has returned with status code: %s: %s",
                                uri, HttpStatus.NOT_FOUND, MESSAGE_BUS_NOT_FOUND_EXCEPTION)));

        LOGGER.debug("GET request to {} has returned with status code: {}, and body: {}",
                uri, responseEntity.getStatusCode(), responseEntity.getBody());

        return responseEntity //NOSONAR responseEntity can not be null, checked above via blockOptional
                .getBody()
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(MESSAGE_BUS_NOT_FOUND_EXCEPTION))
                .getId();
    }

    /**
     * Caching the message schema.
     * @param webClient to get the message schema
     * @return {@link List}: the cache
     */
    @Bean
    public List<String> dataCatalogCache(final WebClient webClient) {
        final String uri = String.format("/catalog/v2/message-schema?dataSpace=%s", PM_KPIS);
        LOGGER.debug("Sending GET request to DataCatalog on uri: {}", uri);

        try {
            final Optional<ResponseEntity<List<MessageSchemaDto>>>  responseEntity = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<List<MessageSchemaDto>>() {})
                    .onErrorResume(
                            WebClientResponseException.class, ex -> ex.getRawStatusCode() == 404 ? Mono.empty() : Mono.error(ex))
                    .blockOptional(Duration.ofSeconds(10));

            responseEntity.ifPresentOrElse(
                    response -> LOGGER.debug("GET request to {} has returned with status code: {}, and body: {}",
                            uri, response.getStatusCode(), response.getBody()),
                    () -> LOGGER.debug("GET request to {} has returned with status code: {}", uri, HttpStatus.NOT_FOUND));
            final List<MessageSchemaDto> body = responseEntity.isPresent() ? responseEntity.get().getBody() : Collections.emptyList();

            return body.stream() //NOSONAR Nullcheck performed above
                       .map(messageSchemaDto -> {
                           final String cachedSchema = String.format("%s:%s",
                                    messageSchemaDto.getDataType().getSchemaName(),
                                    messageSchemaDto.getDataType().getSchemaVersion());
                           LOGGER.debug("Schema Cached: {}", cachedSchema);
                           return cachedSchema;
                       })
                       .collect(Collectors.toList());
        } catch (final IllegalStateException e) {
            LOGGER.warn("Blocking method timed out for dc cache: ", e);
            return Collections.emptyList();
        }
    }
}
