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

package com.ericsson.oss.air.pm.stats.exporter.reader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.stereotype.Component;

import com.google.common.base.Stopwatch;

import lombok.NonNull;

/**
 * Component responsible for reading data from the database. The functionality of this class is
 * to read from a given table with given column names and send back the data.
 */
@Component
@EnableRetry
@ConditionalOnProperty("datasource.enabled")
public class PostgresReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresReader.class);
    private static final String SCHEMA = "kpi";
    private static final String AGGREGATION_BEGIN_TIME = "aggregation_begin_time";
    private static final List<String> TIMESTAMP_COLUMNS = List.of(
            AGGREGATION_BEGIN_TIME,
            "aggregation_end_time");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Qualifier("completed-timestamp_stopwatch")
    private Stopwatch completedTimestampStopwatch;

    /**
     * Method receives a table name, a list of column names and a timestamp params.
     * Calls the queryForJdbcTemplate() method to create an SQL query. Runs the jdbcTemplate.queryForList()
     * method with the built query and sends back the result of the sql call.
     * If the result is not empty, it will log the result, if it is empty it will log a WARN message.
     *
     * @param tableName       String containing the table name
     * @param columnsToExport List of Strings containing the names of columns to export
     * @param timestamp       String containing a TIMESTAMP condition to filter with
     * @return the data fetched is represented by a List, where every element in the list represents a row in the table.
     *         The rows are in the format of key-value pairs (columnName=columnValue) handled by Maps.
     */
    public List<Map<String, Object>> readDb(final String tableName, final List<String> columnsToExport, final String timestamp) {
        if (tableName == null || columnsToExport == null || timestamp == null) {
            return Collections.emptyList();
        }

        try {
            List<Map<String, Object>> result = null;
            final String sql = queryForJdbcTemplate(tableName, columnsToExport);
            final Timestamp sqlTimeStamp = Timestamp.valueOf(LocalDateTime.ofEpochSecond(Long.parseLong(timestamp), 0, ZoneOffset.UTC));
            final Object[] params = new Object[]{sqlTimeStamp};
            completedTimestampStopwatch.stop();
            result = jdbcTemplate.queryForList(sql, params);
            completedTimestampStopwatch.start();

            if (result.isEmpty()) {
                LOGGER.warn("Query returned empty.");
            } else {
                result.forEach(elem -> TIMESTAMP_COLUMNS.forEach(timestampColumn -> {
                    if (elem.containsKey(timestampColumn)) {
                        elem.put(timestampColumn, ((Timestamp) elem.get(timestampColumn)).toLocalDateTime().toEpochSecond(ZoneOffset.UTC));
                    }
                }));
                LOGGER.debug("Exported data: {}", result);
            }
            return result;
        } catch (final BadSqlGrammarException be) {
            LOGGER.warn("Problem occurred while querying the SQL.", be);
            return Collections.emptyList();
        }
    }

    private String queryForJdbcTemplate(final String tableName, final List<String> columns) {
        final List<String> quotedColumns = columns.stream()
                .map(i -> '"' + i.replace("\"", "") + '"')
                .collect(Collectors.toList());
        final StringBuilder query = new StringBuilder().append("SELECT ")
                .append(String.join(",", quotedColumns))
                .append(" FROM  ")
                .append(SCHEMA)
                .append(".\"")
                .append(tableName.replace("\"", ""))
                .append('"')
                .append(" WHERE ")
                .append(AGGREGATION_BEGIN_TIME)
                .append(" = ?");

        LOGGER.info("Built SQL query: {}", query);
        return query.toString();
    }

    /**
     * Method receives a table name and a list of column names. It queries the given table's metadata
     * and gives back the data types of the given columns.
     * @param tableName String containing the table name
     * @param columns   List of Strings containing the names of columns in the table
     * @return a Map where the Key represents a column name in the table and the Value represents the data type of the column.
     */
    public Map<String, String> getColumnTypes(@NonNull final String tableName, final List<String> columns) throws SQLException {
        final List<String> filteredColumns = columns.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        try (Connection connection = dataSource.getConnection();
                ResultSet columnsMetaData = connection.getMetaData().getColumns(null, SCHEMA, tableName.toLowerCase(Locale.ROOT), null)) {
            final Map<String, String> columnTypes = new HashMap<>();
            while (columnsMetaData.next()) {
                if (filteredColumns.stream().anyMatch(columnsMetaData.getString("COLUMN_NAME")::equalsIgnoreCase)) {
                    columnTypes.put(columnsMetaData.getString("COLUMN_NAME"),
                            columnsMetaData.getString("TYPE_NAME"));
                }
            }
            if (columnTypes.isEmpty()) {
                LOGGER.warn("There was an error during querying data types of table {} with columns {}", tableName, columns);
            } else {
                LOGGER.info("There are the following column name - data type pairs in table {}: {}", tableName, columnTypes);
            }
            return columnTypes;
        }
    }
}
