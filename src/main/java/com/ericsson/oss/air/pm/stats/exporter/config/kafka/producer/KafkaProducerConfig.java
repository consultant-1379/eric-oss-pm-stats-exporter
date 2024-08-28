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

package com.ericsson.oss.air.pm.stats.exporter.config.kafka.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.MicrometerProducerListener;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.RecordNameStrategy;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Configuration class for the Kafka producer related Bean constructions.
 */
@EnableKafka
@Configuration
@ConditionalOnProperty("kafka.enabled")
public class KafkaProducerConfig {

    @Value("${kafka.producer.avro.linger.ms}")
    private int avroLingerMs;

    @Value("${kafka.producer.avro.batch.size}")
    private int avroBatchSize;

    @Value("${kafka.producer.avro.compression.type}")
    private String avroCompressionType;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.producer.innerState.transactional.id}")
    private String innerStateTransactionalId;

    @Value("${kafka.properties.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${kafka.producer.avro.transaction-id-prefix}")
    private String transactionIdPrefix;

    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * Configuration Bean for avro producer.
     * @return A Map containing the configurations as properties
     */
    @Bean
    public Map<String, Object> avroProducerConfigs() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistryUrl);
        props.put(KafkaAvroSerializerConfig.VALUE_SUBJECT_NAME_STRATEGY, RecordNameStrategy.class.getName());
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 120000); //2 minutes
        props.put(ProducerConfig.LINGER_MS_CONFIG, avroLingerMs);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, avroBatchSize);
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, avroCompressionType);
        return props;
    }

    /**
     * Kafka producer factory for the avro producer.
     * @return DefaultKafkaProducerFactory to be autowired to the avro producer
     */
    @Bean
    public ProducerFactory<String, GenericRecord> avroProducerFactory() {
        final Map<String, Object> props = avroProducerConfigs();

        final DefaultKafkaProducerFactory<String, GenericRecord> producerFactory =
                new DefaultKafkaProducerFactory<>(props);
        producerFactory.setTransactionIdPrefix(transactionIdPrefix + UUID.randomUUID() + "-");
        producerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
        return producerFactory;
    }

    /**
     * KafkaTemplate created for the avro producer.
     * @return KafkaTemplate to be autowired to the avro producer
     */
    @Bean
    public KafkaTemplate<String, GenericRecord> avroKafkaTemplate() {
        final KafkaTemplate<String, GenericRecord> t = new KafkaTemplate<>(avroProducerFactory());
        t.setObservationEnabled(true);
        return t;
    }

    /**
     * Producer configurations for the json producer.
     * @return A Map containing the configurations as properties
     */
    @Bean
    public Map<String, Object> jsonProducerConfigs() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, innerStateTransactionalId);
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 60000); //1 minute
        return props;
    }

    /**
     * Kafka producer factory for the json producer.
     * @return DefaultKafkaProducerFactory to be autowired to the json producer
     */
    @Bean
    public ProducerFactory<String, Object> jsonProducerFactory() {
        final DefaultKafkaProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(jsonProducerConfigs());
        producerFactory.addListener(new MicrometerProducerListener<>(meterRegistry));
        return producerFactory;
    }

    /**
     * KafkaTemplate created for the json producer.
     * @return KafkaTemplate to be autowired to the json producer
     */
    @Bean
    public KafkaTemplate<String, Object> jsonProducerTemplate() {
        final KafkaTemplate<String, Object> t = new KafkaTemplate<>(jsonProducerFactory());
        t.setObservationEnabled(true);
        return t;
    }

    /**
     * This transaction manager is responsible, for handling the transaction initiated by the completed timestamps topic.
     * @return KafkaTransactionManager used by Spring's @Transactional annotation
     */
    @Bean
    public KafkaTransactionManager<String, GenericRecord> completedTimestampTransactionManager() {
        return new KafkaTransactionManager<>(avroProducerFactory());
    }

    /**
     * This transaction manager is responsible, for handling the transaction initiated by the execution report topic.
     * @return KafkaTransactionManager used by Spring's @Transactional annotation
     */
    @Bean
    public KafkaTransactionManager<String, Object> innerStateTransactionManager() {
        return new KafkaTransactionManager<>(jsonProducerFactory());
    }
}