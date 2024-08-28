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

package com.ericsson.oss.air.pm.stats.exporter.config.kafka.topics;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Configuration class for the Kafka topics related Bean constructions.
 */
@EnableKafka
@Configuration
@ConditionalOnProperty("kafka.enabled")
public class KafkaTopics {
    private static final String TOPIC_NAME = "topic-name";
    private static final String PARTITIONS = "partitions";
    private static final String REPLICATION_FACTOR = "replication-factor";

    /**
     * Bean to easily access the configuration of kafka topics.
     * @return {@link Map} containing the properties as key-value pairs
     */
    @Bean
    @ConfigurationProperties(prefix = "kafka.topics")
    public Map<String, Map<String, String>> topicConfiguration() {
        return new HashMap<>();
    }

    /**
     * Topic for backup.
     * @param topicConfiguration topic's configuration
     * @return the new topic
     */
    @Bean("beanForBackup")
    public NewTopic topicForBackup(final Map<String, Map<String, String>> topicConfiguration) {
        final Map<String, String> backupConfig = topicConfiguration.get("backup");
        return TopicBuilder.name(backupConfig.get(TOPIC_NAME))
                .partitions(Integer.parseInt(backupConfig.get(PARTITIONS)))
                .replicas(Short.parseShort(backupConfig.get(REPLICATION_FACTOR)))
                .compact()
                .config(TopicConfig.DELETE_RETENTION_MS_CONFIG, "100")
                .config(TopicConfig.SEGMENT_MS_CONFIG, "100")
                .build();
    }

    /**
     * Topic for completed timestamp.
     * @param topicConfiguration topic's configuration
     * @return the new topic
     */
    @Bean("beanForCompletedTimestamp")
    public NewTopic topicForCompletedTimestamp(final Map<String, Map<String, String>> topicConfiguration) {
        final Map<String, String> completedTimestampsConfig = topicConfiguration.get("completed-timestamp");
        return TopicBuilder.name(completedTimestampsConfig.get(TOPIC_NAME))
                .partitions(Integer.parseInt(completedTimestampsConfig.get(PARTITIONS)))
                .replicas(Short.parseShort(completedTimestampsConfig.get(REPLICATION_FACTOR)))
                .build();
    }

    /**
     * Topic for scheduled.
     * @param topicConfiguration topic's configuration
     * @return the new topic
     */
    @Bean("beanForScheduled")
    public NewTopic topicForScheduled(final Map<String, Map<String, String>> topicConfiguration) {
        final Map<String, String> scheduledConfig = topicConfiguration.get("scheduled");
        return TopicBuilder.name(scheduledConfig.get(TOPIC_NAME))
                .partitions(Integer.parseInt(scheduledConfig.get(PARTITIONS)))
                .replicas(Short.parseShort(scheduledConfig.get(REPLICATION_FACTOR)))
                .build();
    }

    /**
     * Topic for on demand.
     * @param topicConfiguration topic's configuration
     * @return the new topic
     */
    @Bean("beanForOnDemand")
    public NewTopic topicForOnDemand(final Map<String, Map<String, String>> topicConfiguration) {
        final Map<String, String> onDemandConfig = topicConfiguration.get("on-demand");
        return TopicBuilder.name(onDemandConfig.get(TOPIC_NAME))
                .partitions(Integer.parseInt(onDemandConfig.get(PARTITIONS)))
                .replicas(Short.parseShort(onDemandConfig.get(REPLICATION_FACTOR)))
                .build();
    }
}