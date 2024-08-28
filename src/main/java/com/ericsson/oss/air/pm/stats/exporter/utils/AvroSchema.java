/*******************************************************************************
 * COPYRIGHT Ericsson 2022
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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.postgresql.jdbc.PgArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;

import lombok.NonNull;

/**
 * Class for dynamic Avro schema creation.
 */
@Component
public class AvroSchema {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvroSchema.class);

    /**
     * Implementation for a dynamic avro schema creation.
     * @param tableName  the name of the table which for the schema is created.
     * @param dimensions a Map containing the types of the dimension columns.
     * @param kpis       a Map containing the types of the kpi columns.
     * @return Schema new Avro schema built with the given parameters.
     */
    public Schema buildAvroSchema(@NonNull final String tableName, final Map<String, String> dimensions, final Map<String, String> kpis) {
        if (dimensions == null || kpis == null) {
            throw new IllegalArgumentException("Either the dimension or the KPIs aren't present");
        }
        final SchemaBuilder.FieldAssembler<Schema> assembler = SchemaBuilder.record(tableName).fields();
        assembleDimensionFields(dimensions, assembler);
        assembleKpiFields(kpis, assembler);
        final Schema schema = assembler.endRecord();
        LOGGER.info("Created Schema: {}", schema);
        return schema;
    }

    /**
     * Implementation for creating a list of GenericRecords.
     * @param data    the exported data from the database
     * @param schema  schema built for the data
     * @return a list containing GenericRecords
     */
    public List<GenericRecord> fillRecordWithData(final List<Map<String, Object>> data, @NonNull final Schema schema) {
        final List<GenericRecord> records  = new ArrayList<>();
        for (final Map<String, Object> oneRowOfData : data) {
            final GenericRecord record = new GenericData.Record(schema);
            oneRowOfData.forEach((key, value) -> {
                if (value instanceof PgArray) {
                    try {
                        final PgArray arrayValue = (PgArray) value;
                        record.put(key, new ArrayList<Object>(Arrays.asList((Object[]) arrayValue.getArray())));
                    } catch (final SQLException e) {
                        throw new RollBackException("Array type not supported", e);
                    }
                } else {
                    record.put(key, value);
                }
            });
            records.add(record);
        }
        return records;
    }

    private void assembleDimensionFields(final Map<String, String> dimensions,
                                         final SchemaBuilder.FieldAssembler<Schema> assembler) {
        for (final Map.Entry<String, String> entry : dimensions.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            if (key.toLowerCase(Locale.ROOT).matches("aggregation_(begin|end)_time")) {
                assembler.name(key).type().longType().noDefault();
            } else {
                final DatabaseType type = DatabaseType.fromString(value);
                switch (type) {
                    case VARCHAR:
                        assembler.name(key).type().nullable().stringType().noDefault();
                        break;
                    case BOOLEAN:
                        assembler.name(key).type().nullable().booleanType().noDefault();
                        break;
                    case DOUBLE:
                        assembler.name(key).type().nullable().doubleType().noDefault();
                        break;
                    case FLOAT:
                        assembler.name(key).type().nullable().floatType().noDefault();
                        break;
                    case INT:
                        assembler.name(key).type().nullable().intType().noDefault();
                        break;
                    case LONG:
                        assembler.name(key).type().nullable().longType().noDefault();
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Uncovered type for column %s which was: %s", key, value));
                }
            }
        }
    }

    private void assembleKpiFields(final Map<String, String> kpis,
                                   final SchemaBuilder.FieldAssembler<Schema> assembler) {
        for (final Map.Entry<String, String> entry : kpis.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            final DatabaseType type = DatabaseType.fromString(value);
            switch (type) {
                case DOUBLE:
                    assembler.name(key).type().unionOf().nullType().and().doubleType().endUnion().nullDefault();
                    break;
                case VARCHAR:
                    assembler.name(key).type().unionOf().nullType().and().stringType().endUnion().nullDefault();
                    break;
                case INT:
                    assembler.name(key).type().unionOf().nullType().and().intType().endUnion().nullDefault();
                    break;
                case LONG:
                    assembler.name(key).type().unionOf().nullType().and().longType().endUnion().nullDefault();
                    break;
                case BOOLEAN:
                    assembler.name(key).type().unionOf().nullType().and().booleanType().endUnion().nullDefault();
                    break;
                case FLOAT:
                    assembler.name(key).type().unionOf().nullType().and().floatType().endUnion().nullDefault();
                    break;
                case DOUBLE_ARRAY:
                    assembler.name(key).type().unionOf().nullType().and().array().items().doubleType().endUnion().nullDefault();
                    break;
                case BOOLEAN_ARRAY:
                    assembler.name(key).type().unionOf().nullType().and().array().items().booleanType().endUnion().nullDefault();
                    break;
                case INT_ARRAY:
                    assembler.name(key).type().unionOf().nullType().and().array().items().intType().endUnion().nullDefault();
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Uncovered type for column %s which was: %s", key, value));
            }
        }
    }
}
