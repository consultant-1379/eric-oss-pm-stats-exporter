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

package com.ericsson.oss.air.pm.stats.exporter.config.kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.MicrometerConsumerListener;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Configs for kafka consumer.
 */
@EnableKafka
@Configuration
@ConditionalOnProperty("kafka.enabled")
public class KafkaConsumerConfigs {

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${kafka.topics.backup.group-id}")
    private String backupTopicGroupId;

    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * Receives deserialization exceptions, logs this exception, and commits the consumer offset,
     * so it will not be stuck on a bad message.
     * @return CommonLoggingErrorHandler bean that is required by the Kafka listener container factories.
     */
    @Bean
    public CommonLoggingErrorHandler errorHandler() {
        return new CommonLoggingErrorHandler();
    }

    /**
     * Consumer configuration Bean.
     * @return A Map containing the configurations as properties
     */
    @Bean
    public Map<String, Object> consumerProperties() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.ericsson.oss.air.pm.stats.exporter.model.*");
        return props;
    }

    /**
     * Kafka consumer factory for the execution report topic's listener.
     * @return ConsumerFactory to be autowired to the execution report consumer
     */
    @Bean
    public ConsumerFactory<String, ExecutionReport> executionReportConsumerFactory() {
        final DefaultKafkaConsumerFactory<String, ExecutionReport> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties());
        consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
        return consumerFactory;
    }

    /**
     * Kafka consumer factory for the completed timestamp topic's listener.
     * @return ConsumerFactory to be autowired to the completed timestamp consumer
     */
    @Bean
    public ConsumerFactory<String, ColumnsAndTimeStamps> completedTimestampConsumerFactory() {
        final Map<String, Object> props = consumerProperties();
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 150000); //2.5 minutes
        final DefaultKafkaConsumerFactory<String, ColumnsAndTimeStamps> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
        return consumerFactory;
    }

    /**
     * Kafka consumer factory for the backup topic's listener.
     * @return ConsumerFactory to be autowired to the backup consumer
     */
    @Bean
    public ConsumerFactory<String, Status> backupConsumerFactory() {
        final Map<String, Object> props = consumerProperties();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, backupTopicGroupId + "-" + UUID.randomUUID());
        final DefaultKafkaConsumerFactory<String, Status> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.addListener(new MicrometerConsumerListener<>(meterRegistry));
        return consumerFactory;
    }

    /**
     * Kafka consumer container factory for the execution report topic's listener.
     * @param innerStateTransactionManager transaction manager for Inner State
     * @return ConcurrentKafkaListenerContainerFactory to be autowired to the execution report consumer
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ExecutionReport> executionReportListenerContainerFactory(
            final KafkaTransactionManager<String, Object> innerStateTransactionManager) {
        final ConcurrentKafkaListenerContainerFactory<String, ExecutionReport> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setConsumerFactory(executionReportConsumerFactory());
        factory.getContainerProperties().setTransactionManager(innerStateTransactionManager);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Kafka consumer container factory for the completed timestamp topic's listener.
     * @param completedTimestampTransactionManager transaction manager for Complated Timestamp
     * @return ConcurrentKafkaListenerContainerFactory to be autowired to the completed timestamp consumer
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ColumnsAndTimeStamps> completedTimestampListenerContainerFactory(
            final KafkaTransactionManager<String, GenericRecord> completedTimestampTransactionManager) {
        final ConcurrentKafkaListenerContainerFactory<String, ColumnsAndTimeStamps> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setConsumerFactory(completedTimestampConsumerFactory());
        factory.getContainerProperties().setTransactionManager(completedTimestampTransactionManager);
        factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    /**
     * Kafka consumer container factory for the backup topic's listener.
     * @return ConcurrentKafkaListenerContainerFactory to be autowired to the backup consumer
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Status> backupListenerContainerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, Status> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(backupConsumerFactory());
        factory.setCommonErrorHandler(errorHandler());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}