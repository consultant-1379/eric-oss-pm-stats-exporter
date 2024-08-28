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

package com.ericsson.oss.air.pm.stats.exporter.helpers.utils;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.air.pm.stats.exporter.model.report.ColumnsAndTimeStamps;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReport;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;
import com.ericsson.oss.air.pm.stats.exporter.model.report.Table;

/**
 * Configuration class for testing purposes, with definitions and to overwrite some beans.
 */
public final class TestConstants {
    public static final String SCHEDULED_TOPIC_NAME = "scheduled-test";
    public static final String CTS_TOPIC_NAME = "pm-stats-exporter-json-completed-timestamp-test";

    public static final String PG_USERNAME = "postgres_user";
    public static final String PG_PASSWORD = "super_secret_password";

    public static final ExecutionReport.Scheduling SCHEDULED = ExecutionReport.Scheduling.SCHEDULED;
    public static final ExecutionReport.Scheduling ON_DEMAND = ExecutionReport.Scheduling.ON_DEMAND;

    public static final String KPI_CELL_SECTOR_60 = "kpi_cell_sector_60";

    public static final Integer HOURLY = 60;

    public static final String DIM1 = "dim1";
    public static final String DIM2 = "dim2";
    public static final String AGGREGATION_BEGIN_TIME = "aggregation_begin_time";
    public static final String AGGREGATION_END_TIME = "aggregation_end_time";

    public static final String KPI1 = "kpi1";
    public static final String KPI2 = "kpi2";
    public static final String KPI3 = "kpi3";
    public static final String KPI4 = "kpi4";

    public static final Long   TS_2022_01_12_02_00_00 = 1641952800L;
    public static final String   TS_2022_01_12_02_00_00_S = "1641952800";

    public static final Long   TS_2022_05_17_12_00_00 = 1652788800L;
    public static final String TS_2022_05_17_12_00_00_S = "1652788800";
    public static final Long   TS_2022_05_17_13_00_00 = 1652792400L;
    public static final String TS_2022_05_17_13_00_00_S = "1652792400";
    public static final Long   TS_2022_01_12_04_00_00 = 1641960000L;
    public static final String   TS_2022_01_12_04_00_00_S = "1641960000";

    public static final List<String> DIMENSIONS = List.of(DIM1, DIM2);
    public static final List<String> KPIS = List.of(KPI1, KPI2, KPI3);
    public static final String EXECUTION_ID = "b3ea0e28-6eca-4003-9cb6-77c85c78288a";

    public static final ColumnsAndTimeStamps DEFAULT_COLUMNS_AND_TIMESTAMPS = new ColumnsAndTimeStamps(
            DIMENSIONS, KPIS, SCHEDULED, TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, KPI_CELL_SECTOR_60,
            EXECUTION_ID
    );

    private TestConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Helper function to build a default ExecutionReport in the tests.
     * @return ExecutionReport object, filled with default data
     */
    public static ExecutionReport buildDefaultExecutionReport() {
        final ExecutionReport report = new ExecutionReport();
        buildDefaultExecutionReport(report);
        return report;
    }

    /**
     * Helper function to build a default ExecutionReport in the tests.
     * @return ExecutionReport object, filled with default data
     */
    public static void buildDefaultExecutionReport(final ExecutionReport report) {
        report.setExecutionId(EXECUTION_ID);
        report.setScheduled(SCHEDULED);
        report.setExecutionStart(TS_2022_05_17_12_00_00);
        report.setExecutionEnd(TS_2022_05_17_13_00_00);
        report.setExecutionGroup("my_exec_group");

        report.setTables(new ArrayList<>(List.of(new Table(KPI_CELL_SECTOR_60, HOURLY,
                new ArrayList<>(List.of("agg_column_1", "agg_column_2", AGGREGATION_BEGIN_TIME, AGGREGATION_END_TIME)),
                new ArrayList<>(List.of(KPI1, KPI2)),
                new ArrayList<>(List.of(new ExecutionReportKpi(KPI1, false, TS_2022_01_12_02_00_00, true, TS_2022_01_12_04_00_00),
                    new ExecutionReportKpi(KPI2, false, TS_2022_01_12_02_00_00, true, TS_2022_01_12_04_00_00)))))));
    }
}
