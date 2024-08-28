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

package com.ericsson.oss.air.pm.stats.exporter.helpers.config;

import java.util.Collections;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;

import com.ericsson.oss.air.pm.stats.exporter.config.retry.RetryTemplateUtil;
import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;

/**
 * Configuration class to instantiate the retry template beans for tests.
 */
@EnableRetry
@TestConfiguration
public class TestRetryConfigurations {
    /**
     * RetryTemplate if Postgres is not available on startup.
     * @return RetryTemplate, with backoff-s as configured for Postgres
     */
    @Bean
    public RetryTemplate postgresConnectionRetryTemplate() {
        return RetryTemplateUtil.buildRetryTemplate(
                500,
                5,
                Collections.singletonList(RollBackException.class)
        );
    }

    /**
     * RetryTemplate if Kafka is not available on startup.
     * @return RetryTemplate, with backoff-s as configured for Kafka
     */
    @Bean
    public RetryTemplate kafkaAdminRetryTemplate() {
        return RetryTemplateUtil.buildRetryTemplate(
                500,
                5,
                Collections.singletonList(RollBackException.class)
        );
    }

    /**
     * RetryTemplate if the necessary topics are not available on startup.
     * @return RetryTemplate, with backoff-s as configured for Kafka
     */
    @Bean
    public RetryTemplate checkTopicsRetryTemplate() {
        return RetryTemplateUtil.buildRetryTemplate(
                500,
                5,
                Collections.singletonList(RollBackException.class)
        );
    }

    /**
     * RetryTemplate if the SchemaRegistry is not available on startup.
     * @return RetryTemplate, with backoff-s as configured for SchemaRegistry
     */
    @Bean
    public RetryTemplate schemaRegistryRetryTemplate() {
        return RetryTemplateUtil.buildRetryTemplate(
                100,
                1,
                Collections.singletonList(RollBackException.class)
        );
    }
}
