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

package com.ericsson.oss.air.pm.stats.exporter.reader;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.google.common.base.Stopwatch;

import lombok.SneakyThrows;

@SpringBootTest(classes = {PostgresReader.class})
@ActiveProfiles("postgresTest")
@MockBeans({@MockBean(value = Stopwatch.class, name = "completed-timestamp_stopwatch")})
@ExtendWith({OutputCaptureExtension.class})
class PostgresReaderTest {
    private static final String TIMESTAMP = "1677664800";
    private static final String NOT_IMPORTANT = "not important string";
    private static final String AGGREGATION_BEGIN_TIME = "aggregation_begin_time";
    private static final String AGGREGATION_END_TIME = "aggregation_end_time";
    private static final String INTEGER = "int4";
    private static final String LONG = "int8";
    private static final String FLOAT = "float4";

    private static final List<Map<String, Object>> DATA = List.of(
        new HashMap<>(Map.of(AGGREGATION_BEGIN_TIME, Timestamp.valueOf("2023-03-01 00:00:00"),
            AGGREGATION_END_TIME, Timestamp.valueOf("2023-03-01 01:00:00"), KPI1, 69, KPI2, 420)),
        new HashMap<>(Map.of(AGGREGATION_BEGIN_TIME, Timestamp.valueOf("2023-03-01 01:00:00"),
            AGGREGATION_END_TIME, Timestamp.valueOf("2023-03-01 02:00:00"), KPI1, 69, KPI2, 420)),
        new HashMap<>(Map.of(AGGREGATION_BEGIN_TIME, Timestamp.valueOf("2023-03-01 02:00:00"),
            AGGREGATION_END_TIME, Timestamp.valueOf("2023-03-01 03:00:00"), KPI1, 69, KPI2, 420)),
        new HashMap<>(Map.of(AGGREGATION_BEGIN_TIME, Timestamp.valueOf("2023-03-01 03:00:00"),
            AGGREGATION_END_TIME, Timestamp.valueOf("2023-03-01 04:00:00"), KPI1, 69, KPI2, 420))
    );

    private static final List<Map<String, Object>> DATA_TRANSFORMED = List.of(
            Map.of(AGGREGATION_BEGIN_TIME, 1677628800L, AGGREGATION_END_TIME, 1677632400L, KPI1, 69, KPI2, 420),
            Map.of(AGGREGATION_BEGIN_TIME, 1677632400L, AGGREGATION_END_TIME, 1677636000L, KPI1, 69, KPI2, 420),
            Map.of(AGGREGATION_BEGIN_TIME, 1677636000L, AGGREGATION_END_TIME, 1677639600L, KPI1, 69, KPI2, 420),
            Map.of(AGGREGATION_BEGIN_TIME, 1677639600L, AGGREGATION_END_TIME, 1677643200L, KPI1, 69, KPI2, 420)
    );

    private static final List<String> COLUMN_NAMES = List.of(KPI1, KPI2, KPI3, AGGREGATION_BEGIN_TIME, AGGREGATION_END_TIME);
    private static final Map<String, String> COLUMN_TYPES = Map.of(
            KPI1, INTEGER,
            KPI2, LONG,
            KPI3, FLOAT,
            AGGREGATION_BEGIN_TIME, LONG,
            AGGREGATION_END_TIME, LONG
    );

    @Mock
    private ResultSet resultSetMock;

    @Mock
    private Connection connectionMock;

    @Mock
    private DatabaseMetaData databaseMetaDataMock;

    @MockBean
    private JdbcTemplate jdbcTemplateMock;

    @MockBean
    private DataSource dataSourceMock;

    @Autowired
    private PostgresReader postgresReader;

    @Test
    void whenReadDbCalledWithGoodData_shouldSaidDataBeReturnedCorrectly() {
        doReturn(DATA).when(jdbcTemplateMock).queryForList(anyString(), any(Object.class));

        final List<Map<String, Object>> actual = postgresReader.readDb(NOT_IMPORTANT, Collections.emptyList(), TIMESTAMP);

        assertThat(actual).containsExactlyInAnyOrderElementsOf(DATA_TRANSFORMED);
    }

    @ParameterizedTest(name = "[{index}], tableName: ''{0}'', columnsToExport: ''{1}'', timestamp: ''{2}''")
    @MethodSource("readDbArgumentGenerator")
    void whenReadDbCalledWithNullValue_shouldEmptyListBeReturned(final String tableName, final List<String> columnsToExport, final String timestamp) {
        final List<Map<String, Object>> actual = postgresReader.readDb(tableName, columnsToExport, timestamp);

        assertThat(actual).isEmpty();
    }

    @Test
    void whenReadDbCalledWithInvalidDataCausingException_shouldWarnBeShowedAndEmptyListReturned(final CapturedOutput output) {
        doThrow(BadSqlGrammarException.class).when(jdbcTemplateMock).queryForList(anyString(), any(Object.class));

        final List<Map<String, Object>> actual = postgresReader.readDb(NOT_IMPORTANT, Collections.emptyList(), TIMESTAMP);

        assertThat(actual).isEmpty();
        assertThat(output.getOut()).contains("Problem occurred while querying the SQL.");
    }

    @Test
    void whenReadDbCalledWithInvalidDataWhichIsNOtPresentInDb_shouldWarnBeShowedAndEmptyListReturned(final CapturedOutput output) {
        doReturn(Collections.emptyList()).when(jdbcTemplateMock).queryForList(anyString(), any(Object.class));

        final List<Map<String, Object>> actual = postgresReader.readDb(NOT_IMPORTANT, Collections.emptyList(), TIMESTAMP);

        assertThat(actual).isEmpty();
        assertThat(output.getOut()).contains("Query returned empty.");
    }

    @Test
    @SneakyThrows
    void whenGetColumnTypesCalledNoError_shouldCorrectTypesBeReturned() {
        doReturn(connectionMock).when(dataSourceMock).getConnection();
        doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        doReturn(resultSetMock).when(databaseMetaDataMock).getColumns(any(), anyString(), anyString(), any());
        doReturn(true, true, true, true, true, false).when(resultSetMock).next();
        doReturn(KPI1, KPI1, KPI2, KPI2, KPI3, KPI3, AGGREGATION_BEGIN_TIME, AGGREGATION_BEGIN_TIME, AGGREGATION_END_TIME, AGGREGATION_END_TIME)
                .when(resultSetMock).getString("COLUMN_NAME");
        doReturn(INTEGER, LONG, FLOAT, LONG, LONG).when(resultSetMock).getString("TYPE_NAME");

        final Map<String, String> actual = postgresReader.getColumnTypes(NOT_IMPORTANT, COLUMN_NAMES);

        assertThat(actual).containsAllEntriesOf(COLUMN_TYPES);
    }

    @Test
    @SneakyThrows
    void whenGetColumnTypesCalledWithPostgreError_shouldResultBeEmptyAndWarningAppear(final CapturedOutput output) {
        doReturn(connectionMock).when(dataSourceMock).getConnection();
        doReturn(databaseMetaDataMock).when(connectionMock).getMetaData();
        doReturn(resultSetMock).when(databaseMetaDataMock).getColumns(any(), anyString(), anyString(), any());
        doReturn(false).when(resultSetMock).next();

        final Map<String, String> actual = postgresReader.getColumnTypes(NOT_IMPORTANT, COLUMN_NAMES);

        assertThat(actual).isEmpty();
        assertThat(output.getOut()).contains("There was an error during querying data types");
    }

    private static Stream<Arguments> readDbArgumentGenerator() {
        return Stream.of(
            Arguments.of(NOT_IMPORTANT, Collections.emptyList(), null),
            Arguments.of(NOT_IMPORTANT, null, TIMESTAMP),
            Arguments.of(null, Collections.emptyList(), TIMESTAMP)
        );
    }
}