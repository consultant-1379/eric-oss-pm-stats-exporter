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

package com.ericsson.oss.air.pm.stats.exporter.processor.cts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;
import com.ericsson.oss.air.pm.stats.exporter.reader.PostgresReader;
import com.ericsson.oss.air.pm.stats.exporter.utils.AvroSchema;
import com.ericsson.oss.air.pm.stats.exporter.utils.CustomMetrics;
import com.ericsson.oss.air.pm.stats.exporter.utils.MeterRegistryHelper;
import com.ericsson.oss.air.pm.stats.exporter.writer.DataCatalogWriter;
import com.ericsson.oss.air.pm.stats.exporter.writer.KafkaWriter;

import io.confluent.kafka.schemaregistry.ParsedSchema;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * Class responsible for processing completed timestamp messages and performing the necessary data read
 * and export tasks.
 */
@Component
@ConditionalOnProperty("kafka.enabled")
public class CompletedTimestampsProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompletedTimestampsProcessor.class);
    private static final int EXIT_CODE = 42;

    @Value("${kafka.topics.scheduled.topic-name}")
    private String scheduledTopic;

    @Value("${kafka.topics.on-demand.topic-name}")
    private String onDemandTopic;

    @Value("${meterCollectors.enabled}")
    private boolean collectorsEnabled;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AvroSchema avroSchema;

    @Autowired(required = false)
    private DataCatalogWriter dataCatalogWriter;

    @Autowired
    private KafkaWriter kafkaWriter;

    @Autowired
    private MeterRegistryHelper meterRegistryHelper;

    @Autowired
    private CustomMetrics customMetrics;

    @Autowired
    private PostgresReader postgresReader;

    @Autowired
    private SchemaRegistryClient schemaRegistryClient;

    @Autowired
    private RetryTemplate schemaRegistryRetryTemplate;

    /**
     * Method for processing a completed timestamp message. It first reads from the database, creates the schema and
     * notifies the writer to export with the necessary information. If the transaction was successful, then the consumer
     * offset will be committed, otherwise an error log will be made.
     *
     * @param columnsAndTimeStamps columns and timestamps extracted from the Kafka message record
     * @return true if the data was fetched and sent, false if the database read result was empty
     */
    public boolean processCompletedTimestamps(final ColumnsAndTimeStamps columnsAndTimeStamps) throws SQLException {
        final String topic = columnsAndTimeStamps.getScheduled() == ExecutionReport.Scheduling.SCHEDULED ? scheduledTopic : onDemandTopic;
        final String tableName = columnsAndTimeStamps.getTableName();

        final List<Map<String, Object>> dbQueryResult = readColumnsFromPostgres(
                tableName,
                columnsAndTimeStamps.getColumns(),
                columnsAndTimeStamps.getStartTime());

        if (dbQueryResult.isEmpty()) {
            LOGGER.warn("The database returned with an empty result, therefore nothing was written on the topic: {}", topic);
            meterRegistryHelper.incrementTransaction2EmptyPostgresQueriesCounter();
            return false;
        }

        meterRegistryHelper.increaseTransaction2SuccessfulPostgresQueriesAndReadRowsCounters(dbQueryResult.size());

        final Schema schema = buildAvroSchema(tableName, columnsAndTimeStamps.getDimensions(), columnsAndTimeStamps.getKpis());

        sendToKafka(topic, columnsAndTimeStamps, dbQueryResult, schema);

        writeToDataCatalog(topic, tableName, schema);

        LOGGER.info("The transaction was completed, and the message was sent to topic {}", topic);
        return true;
    }

    /**
     * Method for calling the PostgresReader to get the contents of a given table's selected columns according to
     * the startTime value. If a connection cannot be made, the application should be terminated.
     *
     * @param tableName name of the table
     * @param columns targeted columns of the table
     * @param startTime timestamp value as a filter for the aggregation begin time
     * @return the fetched rows in the format of key-value pairs (columnName=columnValue)
     */
    private List<Map<String, Object>> readColumnsFromPostgres(final String tableName, final List<String> columns, final String startTime)
            throws DataAccessException {
        try {
            return postgresReader.readDb(tableName, columns, startTime);
        } catch (final CannotGetJdbcConnectionException e) {
            jdbcConnectionErrorExit(e);
        }
        return Collections.emptyList();
    }

    /**
     * Method for building the Avro schema required for the data transfer. The column types are provided by
     * Postgres queries. If a Postgres connection cannot be made, the application should be terminated.
     *
     * @param tableName name of the Postgres table
     * @param dimensions list of dimension columns of the table
     * @param kpis list of kpi columns of the table
     * @return Schema new Avro schema built with the given parameters.
     */
    private Schema buildAvroSchema(final String tableName, final List<String> dimensions, final List<String> kpis)
            throws SQLException, IllegalArgumentException {
        try {
            final Map<String, String> dimensionTypes = postgresReader.getColumnTypes(tableName, dimensions);
            final Map<String, String> kpiTypes = postgresReader.getColumnTypes(tableName, kpis);
            return avroSchema.buildAvroSchema(tableName, dimensionTypes, kpiTypes);
        } catch (final CannotGetJdbcConnectionException e) {
            jdbcConnectionErrorExit(e);
        }
        return null;
    }

    /**
     * Method for terminating the application, triggered by Postgres connection errors.
     *
     * @param e the triggering Exception object to be logged
     */
    private void jdbcConnectionErrorExit(final Exception e) {
        LOGGER.error("Database is unavailable and connection wasn't restored in time", e);
        SpringApplication.exit(applicationContext, () -> EXIT_CODE);
    }

    /**
     * This method is responsible for writing messages to the corresponding (Scheduled / On Demand) topic in Kafka.
     *
     * @param topic The name of the topic, where the message should be sent
     * @param columnsAndTimeStamps columns and timestamps extracted from the Kafka message record
     * @param dbQueryResult the exported data from the database
     * @param schema Avro schema built for the current data
     */
    private void sendToKafka(final String topic, final ColumnsAndTimeStamps columnsAndTimeStamps,
                             final List<Map<String, Object>> dbQueryResult, final Schema schema) {
        schemaRegistryRetryTemplate.execute(arg -> {
            kafkaWriter.sendAvro(topic, avroSchema.fillRecordWithData(dbQueryResult, schema));
            return null;
        }, context -> {
            if (context.getLastThrowable() instanceof RollBackException) {
                throw new RollBackException("Could not send message in time: ", context.getLastThrowable());
            } else {
                LOGGER.error("Schema Registry is unavailable and connection wasn't restored in time");
                SpringApplication.exit(applicationContext, () -> EXIT_CODE);
            }
            return null;
        });

        meterRegistryHelper.incrementTransaction2SuccessfulKafkaWritingsCounter();
        meterRegistryHelper.increaseAvroExportedTablesCounter(columnsAndTimeStamps.getTableName(), topic);

        if (collectorsEnabled) {
            final long dttm = System.currentTimeMillis();
            customMetrics.setGauges(
                    CustomMetrics.PREFIX + "cts_avro_export",
                    "Collection of KPI export (AVRO) timestamps",
                    labelValueList(columnsAndTimeStamps, dttm));
        }
    }

    /**
     * This method attempts to parse the schema using the AVRO schema type, if successful then the topic name
     * and the schemaID of the parsed schema are registered in the DataCatalog.
     *
     * @param topic name of the topic
     * @param tableName name of the Postgres table
     * @param schema the compiled Avro schema
     */
    private void writeToDataCatalog(final String topic, final String tableName, final Schema schema) {
        if (dataCatalogWriter != null && schema != null) {
            final Optional<ParsedSchema> parsedSchema = schemaRegistryClient.parseSchema("AVRO", schema.toString(), Collections.emptyList());

            if (parsedSchema.isPresent()) {
                try {
                    dataCatalogWriter.createSchema(topic, tableName, schemaRegistryClient.getVersion(tableName, parsedSchema.get()));
                } catch (final IOException | RestClientException e) {
                    LOGGER.warn("Could not connect to Schema Registry in order to get the schema ID for Data Catalog", e);
                }
            }
        }
    }

    private List<Pair<String, Long>> labelValueList(final ColumnsAndTimeStamps columnsAndTimeStamps, final Long value) {
        return columnsAndTimeStamps.getKpis().stream()
            .map(kpi -> Pair.of(
                String.format("end=%s,id=%s,kpi=%s,scheduled=%s,start=%s,table=%s,",
                        columnsAndTimeStamps.getEndTime(),
                        columnsAndTimeStamps.getTriggerExecutionId(),
                        kpi,
                        columnsAndTimeStamps.getScheduled().name(),
                        columnsAndTimeStamps.getStartTime(),
                        columnsAndTimeStamps.getTableName()),
                value))
            .collect(Collectors.toList());
    }
}