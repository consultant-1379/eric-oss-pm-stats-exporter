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

package com.ericsson.oss.air.pm.stats.exporter.config.kafka.admin;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Configuration class for the Kafka admin related Bean constructions.
 */
@EnableKafka
@Configuration
@ConditionalOnProperty("kafka.enabled")
public class KafkaAdminConfig {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Kafka admin configurations for the topic creation.
     * @return A KafkaAdmin containing the Kafka topic configuration
     */
    @Bean
    public KafkaAdmin admin() {
        final Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        final KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setAutoCreate(false);
        admin.setFatalIfBrokerNotAvailable(true);
        return admin;
    }
}