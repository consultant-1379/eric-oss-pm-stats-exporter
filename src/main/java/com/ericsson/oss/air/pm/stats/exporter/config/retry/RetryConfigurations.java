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

package com.ericsson.oss.air.pm.stats.exporter.config.retry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;

/**
 * Configuration class for the  retry related (Postgres and Kafka) Bean constructions.
 */
@EnableRetry
@Configuration
public class RetryConfigurations {
    private static final String BACKOFF_PERIOD = "backoffPeriod";
    private static final String MAX_ATTEMPTS = "maxAttempts";
    private static final String CAT_KAFKA = "kafka";
    private static final String CAT_POSTGRES = "postgres";
    private static final String CAT_SR = "sr";
    private static final String CAT_TOPICS = "topics";
    private static final String RETRY_CONFIGURATION = "retryConfiguration";

    /**
     * Bean to create the retry configuration for Postgres and Kafka.
     * @return retryConfiguration
     */
    @Bean
    @ConfigurationProperties(prefix = "retry")
    @Qualifier(RETRY_CONFIGURATION)
    public Map<String, Map<String, Integer>> retryConfiguration() {
        return new HashMap<>();
    }

    /**
     * RetryTemplate if Postgres is not available on startup.
     * @param retryConfiguration configuration of retry
     * @return RetryTemplate, with backoff-s as configured for Postgres
     */
    @ConditionalOnProperty("datasource.enabled")
    @Bean
    public RetryTemplate postgresConnectionRetryTemplate(
            @Qualifier(RETRY_CONFIGURATION) final Map<String, Map<String, Integer>> retryConfiguration) {
        return RetryTemplateUtil.buildRetryTemplate(
                retryConfiguration.get(CAT_POSTGRES).get(BACKOFF_PERIOD),
                retryConfiguration.get(CAT_POSTGRES).get(MAX_ATTEMPTS)
        );
    }

    /**
     * RetryTemplate if Kafka is not available on startup.
     * @param retryConfiguration configuration of retry
     * @return RetryTemplate, with backoff-s as configured for Kafka
     */
    @ConditionalOnProperty("kafka.enabled")
    @Bean
    public RetryTemplate kafkaAdminRetryTemplate(
            @Qualifier(RETRY_CONFIGURATION)  final Map<String, Map<String, Integer>> retryConfiguration) {
        return RetryTemplateUtil.buildRetryTemplate(
                retryConfiguration.get(CAT_KAFKA).get(BACKOFF_PERIOD),
                retryConfiguration.get(CAT_KAFKA).get(MAX_ATTEMPTS)
        );
    }

    /**
     * RetryTemplate if the necessary topics are not available on startup.
     * @param retryConfiguration configuration of retry
     * @return RetryTemplate, with backoff-s as configured for Kafka
     */
    @ConditionalOnProperty("kafka.enabled")
    @Bean
    public RetryTemplate checkTopicsRetryTemplate(
            @Qualifier(RETRY_CONFIGURATION)  final Map<String, Map<String, Integer>> retryConfiguration) {
        return RetryTemplateUtil.buildRetryTemplate(
                retryConfiguration.get(CAT_TOPICS).get(BACKOFF_PERIOD),
                retryConfiguration.get(CAT_TOPICS).get(MAX_ATTEMPTS)
        );
    }

    /**
     * RetryTemplate if the SchemaRegistry is not available on startup.
     * @param retryConfiguration configuration of retry
     * @return RetryTemplate, with backoff-s as configured for SchemaRegistry
     */
    @ConditionalOnProperty("kafka.enabled")
    @Bean
    public RetryTemplate schemaRegistryRetryTemplate(
            @Qualifier(RETRY_CONFIGURATION)  final Map<String, Map<String, Integer>> retryConfiguration) {
        return RetryTemplateUtil.buildRetryTemplate(
                retryConfiguration.get(CAT_SR).get(BACKOFF_PERIOD),
                retryConfiguration.get(CAT_SR).get(MAX_ATTEMPTS),
                Collections.singletonList(RollBackException.class)
        );
    }
}