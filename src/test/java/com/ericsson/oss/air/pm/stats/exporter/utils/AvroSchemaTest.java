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

package com.ericsson.oss.air.pm.stats.exporter.utils;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI_CELL_SECTOR_60;
import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.postgresql.jdbc.PgArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.report.exception.RollBackException;

import lombok.SneakyThrows;

@SpringBootTest(classes = {AvroSchema.class})
@ActiveProfiles("test")
class AvroSchemaTest {
    private static final String AGGREGATION_BEGIN_TIME = "aggregation_begin_time";
    private static final String INTEGER_COLUMN = "integerKpiColumn";
    private static final String FLOAT_COLUMN = "floatKpiColumn";
    private static final String INTEGER_ARRAY_COLUMN = "intArrayKpiColumn";
    private static final String INT_8 = "int8";
    private static final Schema SCHEMA = new Schema.Parser().parse(
            "{\"type\":\"record\",\"name\":\"kpi_cell_sector_60\",\"fields\":["
            + "{\"name\":\"aggregation_begin_time\",\"type\":[\"long\",\"null\"]},"
            + "{\"name\":\"floatKpiColumn\",\"type\":[\"null\",\"float\"],\"default\":null},"
            + "{\"name\":\"integerKpiColumn\",\"type\":[\"null\",\"int\"],\"default\":null},"
            + "{\"name\":\"intArrayKpiColumn\",\"type\":[\"null\",{\"type\":\"array\",\"items\":\"int\"}],\"default\":null}]}");

    private static final PgArray PG_ARRAY_MOCK = mock(PgArray.class);

    private static final Map<String, String> DIMENSION_COLUMN_NAME_AND_TYPE_MAP = Map.of(
            "aggregation_begin_time", INT_8,
            "stringDimensionColumn", "VARCHAR",
            "booleanDimensionColumn", "bool",
            "floatDimensionColumn", "float4",
            "doubleDimensionColumn", "float8",
            "intDimensionColumn", "int4",
            "longDimensionColumn", INT_8
    );

    private static final Map<String, String> KPI_COLUMN_NAME_AND_TYPE_MAP = Map.ofEntries(
            entry("doubleKpiColumn", "float8"),
            entry("booleanKpiColumn", "bool"),
            entry("stringKpiColumn", "varchar"),
            entry("longKpiColumn", INT_8),
            entry("integerKpiColumn", "int4"),
            entry("floatKpiColumn", "float4"),
            entry("intArrayKpiColumn", "_int4"),
            entry("doubleArrayKpiColumn", "_float8"),
            entry("booleanArrayKpiColumn", "_bool")
    );

    private static final Map<String, String> TYPE_MAP = Map.ofEntries(
            entry("float8", "double"),
            entry("bool", "boolean"),
            entry("varchar", "string"),
            entry("VARCHAR", "string"),
            entry(INT_8, "long"),
            entry("int4", "int"),
            entry("float4", "float"),
            entry("_int4", "array"),
            entry("_float8", "array"),
            entry("_bool", "array")
    );

    private static final Map<String, String> EXPECTED_SCHEMA = Stream.concat(
            DIMENSION_COLUMN_NAME_AND_TYPE_MAP.entrySet().stream(),
            KPI_COLUMN_NAME_AND_TYPE_MAP.entrySet().stream())
            .collect(Collectors.toMap(Entry::getKey, entry -> TYPE_MAP.get(entry.getValue())));

    private static final List<Map<String, Object>> DATA = List.of(
            Map.of(AGGREGATION_BEGIN_TIME, 896538300L, INTEGER_COLUMN, 69, FLOAT_COLUMN, 42.0f, INTEGER_ARRAY_COLUMN, PG_ARRAY_MOCK),
            Map.of(AGGREGATION_BEGIN_TIME, 896538300L, INTEGER_COLUMN, 42, FLOAT_COLUMN, 420.0f, INTEGER_ARRAY_COLUMN, PG_ARRAY_MOCK),
            Map.of(AGGREGATION_BEGIN_TIME, 896538300L, INTEGER_COLUMN, 420, FLOAT_COLUMN, 69.0f, INTEGER_ARRAY_COLUMN, PG_ARRAY_MOCK)
    );

    private static final List<GenericRecord> EXPECTED_RECORDS = List.of(
            record(896538300L, 69, 42.0f, List.of(69, 420, 42)),
            record(896538300L, 42, 420.0f, List.of(420, 42, 69)),
            record(896538300L, 420, 69.0f, List.of(42, 69, 420))
    );

    @Autowired
    private AvroSchema avroSchema;

    @Test
    void whenBuildAvroSchemaCalled_shouldGetAvroSchemaWithGivenDetails() {
        final Schema dynamicSchema = avroSchema.buildAvroSchema(KPI_CELL_SECTOR_60,
                DIMENSION_COLUMN_NAME_AND_TYPE_MAP, KPI_COLUMN_NAME_AND_TYPE_MAP);

        final Map<String, String> actual = dynamicSchema.getFields().stream()
                .map(AvroSchemaTest::fieldToEntry)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        assertThat(dynamicSchema.getName())
                .isEqualTo(KPI_CELL_SECTOR_60);
        assertThat(actual)
                .containsAllEntriesOf(EXPECTED_SCHEMA);
    }

    @ParameterizedTest(name = "[{index}] dimensions: {0}, kpis: {1}, errorMessage: ''{2}''")
    @MethodSource("argumentGenerator")
    void whenBuildAvroSchemaCalledWithBadParams_shouldExceptionBeThrown(
            final Map<String, String> dimensions,
            final Map<String, String> kpis,
            final String errorMessage
    ) {
        assertThatThrownBy(() -> avroSchema.buildAvroSchema(KPI_CELL_SECTOR_60, dimensions, kpis))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(errorMessage);
    }

    @Test
    @SneakyThrows
    void whenFillRecordWithDataCalled_shouldRecordsBeCorrect() {
        doReturn(new Integer[]{69, 420, 42}, new Integer[]{420, 42, 69}, new Integer[]{42, 69, 420}).when(PG_ARRAY_MOCK).getArray();

        final List<GenericRecord> actual = avroSchema.fillRecordWithData(DATA, SCHEMA);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(EXPECTED_RECORDS);
    }

    @Test
    @SneakyThrows
    void whenFillRecordWithDataCalledButSqlExceptionHappens_shouldRollBackExceptionBeThrown() {
        doThrow(SQLException.class).when(PG_ARRAY_MOCK).getArray();

        assertThatThrownBy(() -> avroSchema.fillRecordWithData(DATA, SCHEMA))
                .isInstanceOf(RollBackException.class)
                .hasMessage("Array type not supported");
    }

    private static Stream<Arguments> argumentGenerator() {
        return Stream.of(
            Arguments.of(DIMENSION_COLUMN_NAME_AND_TYPE_MAP, Map.of("notRecognizable", "other"), "other is not a recognizable type"),
            Arguments.of(Map.of("notRecognizable", "other"), KPI_COLUMN_NAME_AND_TYPE_MAP, "other is not a recognizable type"),
            Arguments.of(Map.of("notCovered", "_float8"), KPI_COLUMN_NAME_AND_TYPE_MAP, "Uncovered type for column notCovered which was: _float8"),
            Arguments.of(DIMENSION_COLUMN_NAME_AND_TYPE_MAP, null, "Either the dimension or the KPIs aren't present"),
            Arguments.of(null, DIMENSION_COLUMN_NAME_AND_TYPE_MAP, "Either the dimension or the KPIs aren't present")
        );
    }

    private static Entry<String, String> fieldToEntry(final Field field) {
        return entry(
            field.name(),
            field.schema().isUnion()
            ? field.schema().getTypes().stream()
                .map(schema -> schema.getType().getName())
                .filter(name -> !name.equals("null"))
                .findFirst()
                .orElse("null")
            : field.schema().getType().getName()
        );
    }

    private static GenericRecord record(
            final Long aggregationBeginTime,
            final Integer integerColumn,
            final Float floatColumn,
            final List<Object> integerArrayColumn
    ) {
        final GenericRecord record = new Record(SCHEMA);
        record.put(AGGREGATION_BEGIN_TIME, aggregationBeginTime);
        record.put(INTEGER_COLUMN, integerColumn);
        record.put(FLOAT_COLUMN, floatColumn);
        record.put(INTEGER_ARRAY_COLUMN, new ArrayList<>(integerArrayColumn));
        return record;
    }
}
