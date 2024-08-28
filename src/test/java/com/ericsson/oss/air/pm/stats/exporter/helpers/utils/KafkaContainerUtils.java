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

package com.ericsson.oss.air.pm.stats.exporter.helpers.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.ericsson.oss.air.pm.stats.exporter.helpers.model.Consumer;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

public final class KafkaContainerUtils {
    private static final String SCHEMA_REGISTRY_URL = "mock://testurl";

    private KafkaContainerUtils() {
        throw new UnsupportedOperationException("This is a utility class, do not instantiate");
    }

    /**
     * Helper method to be used in test, to avoid boilerplate code.
     * @param topic Kafka topic of the consumer
     * @param bootstrapServers The kafka broker, address where the consumer will be created
     * @param isTransactional Boolean value, should be true, if the created consumer should be transactional
     * @return KafkaMessageListenerContainer object, that can be used to consume Avro messages in the tests
     */
    public static KafkaMessageListenerContainer<String, Object> createAvroConsumer(
            final String topic, final String bootstrapServers, final Boolean isTransactional) {
        return createConsumer(topic, bootstrapServers, isTransactional, KafkaAvroDeserializer.class);
    }

    /**
     * Helper method to be used in test, to avoid boilerplate code.
     * @param topic Kafka topic of the consumer
     * @param bootstrapServers The kafka broker, where the consumer will be created
     * @param isTransactional Boolean value, should be true, if the created consumer should be transactional
     * @return KafkaMessageListenerContainer object, that can be used to consume messages in the tests
     */
    public static KafkaMessageListenerContainer<String, Object> createConsumer(
            final String topic, final String bootstrapServers, final Boolean isTransactional) {
        return createConsumer(topic, bootstrapServers, isTransactional, JsonDeserializer.class);
    }

    /**
     * Helper method to be used in test, to avoid boilerplate code.
     * @param topic Kafka topic of the consumer
     * @param bootstrapServers The kafka broker, where the consumer will be created
     * @param isTransactional Boolean value, should be true, if the created consumer should be transactional
     * @param deserializer Deserializer's class
     * @return KafkaMessageListenerContainer object, that can be used to consume messages in the tests
     */
    public static KafkaMessageListenerContainer<String, Object> createConsumer(
            final String topic, final String bootstrapServers, final boolean isTransactional, final Class deserializer) {
        final ContainerProperties containerProperties = new ContainerProperties(topic);
        final Consumer consumer = new Consumer((isTransactional ? "" : "non") + "transactional-consumer");
        containerProperties.setMessageListener(consumer);
        final Map<String, Object> consumerConfigs =
                new HashMap<>(KafkaTestUtils.consumerProps(
                        bootstrapServers, (isTransactional ? "" : "non") + "transactional-consumer", "true"));
        consumerConfigs.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfigs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerConfigs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        consumerConfigs.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isTransactional ? "read_committed" : "read_uncommitted");
        consumerConfigs.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer);
        consumerConfigs.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ericsson.oss.air.pm.stats.exporter.model.*");
        consumerConfigs.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, SCHEMA_REGISTRY_URL);
        final KafkaMessageListenerContainer<String, Object> container = new KafkaMessageListenerContainer<>(
                new DefaultKafkaConsumerFactory<>(consumerConfigs), containerProperties);
        container.start();
        return container;
    }
}
