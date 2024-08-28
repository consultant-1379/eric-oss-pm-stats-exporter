--
-- COPYRIGHT Ericsson 2023
--
--
--
-- The copyright to the computer program(s) herein is the property of
--
-- Ericsson Inc. The programs may be used and/or copied only with written
--
-- permission from Ericsson Inc. or in accordance with the terms and
--
-- conditions stipulated in the agreement/contract under which the
--
-- program(s) have been supplied.
--

-- Used timestamps:
--  1641945600:	2022-01-12 00:00:00
--  1641949200:	2022-01-12 01:00:00
--  1641952800:	2022-01-12 02:00:00
--  1641956400:	2022-01-12 03:00:00
--  1641960000:	2022-01-12 04:00:00
--  1658102400:	2022-07-18 00:00:00
--  1658188800:	2022-07-19 00:00:00
--  1658275200:	2022-07-20 00:00:00
--  1658361600:	2022-07-21 00:00:00
--  1658448000:	2022-07-22 00:00:00
--  1678060800: 2023-03-06 00:00:00
--  1678061700: 2023-03-06 00:00:00

set time zone UTC;

CREATE SCHEMA IF NOT EXISTS kpi;

CREATE TABLE IF NOT EXISTS kpi.kpi_cell_sector_60 (
  aggregation_begin_time TIMESTAMP NULL,
  agg_column_1 VARCHAR(255),
  agg_column_2 VARCHAR(255),
  agg_column_3 VARCHAR(255),
  sum_integer_join1 BIGINT,
  sum_float_join1 DOUBLE PRECISION,
  sum_integer_join2 BIGINT,
  sum_float_join2 DOUBLE PRECISION,
  sum_integer_join3 BIGINT,
  sum_float_join3 DOUBLE PRECISION,
  sum_integer_join4 BIGINT NULL
);

CREATE TABLE IF NOT EXISTS kpi.kpi_cell_sector_1440 (
  aggregation_begin_time TIMESTAMP NULL,
  agg_column_1 VARCHAR(255),
  agg_column_2 VARCHAR(255),
  agg_column_3 VARCHAR(255),
  sum_integer_join1 BIGINT,
  sum_float_join1 DOUBLE PRECISION,
  sum_integer_join2 BIGINT,
  sum_float_join2 DOUBLE PRECISION,
  sum_integer_join3 BIGINT,
  sum_float_join3 DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS kpi.kpi_cell_guid_60 (
  aggregation_begin_time TIMESTAMP NULL,
  agg_column_1 VARCHAR(255),
  agg_column_2 VARCHAR(255),
  agg_column_3 VARCHAR(255),
  sum_integer_join1 BIGINT,
  sum_float_join1 DOUBLE PRECISION,
  sum_integer_join2 BIGINT,
  sum_float_join2 DOUBLE PRECISION,
  sum_integer_join3 BIGINT,
  sum_float_join3 DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS kpi.kpi_cell_sector_15 (
  aggregation_begin_time TIMESTAMP NOT NULL,
  agg_column_1 VARCHAR(255),
  agg_column_2 VARCHAR(255),
  agg_column_3 VARCHAR(255),
  sum_integer_exportable BIGINT,
  sum_float_exportable DOUBLE PRECISION,
  sum_integer_not_exportable BIGINT,
  sum_float_not_exportable DOUBLE PRECISION
);

INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 72, 275.5, 981, -7.75, -389, -612.3, 381);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 72, 275.5, 981, -7.75, -389, -612.3);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -6, -882.658, -16, -51.005, 690, 41.410);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -6, -882.658, -16, -51.005, 690, 41.410);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -94, -854.57, 378, -5.73, 6, -47.35);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -94, -854.57, 378, -5.73, 6, -47.35);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -943, -78.41, 5, 9.2, -720, 487.990);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -943, -78.41, 5, 9.2, -720, 487.990);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 214, 8.709, 0, 62.056, -892, 2.084);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 214, 8.709, 0, 62.056, -892, 2.084);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -30, -189.800, 394, -12.08, 8, -7.784);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 1, -329.6, 9, -76.62, 4, -65.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -7, -3.72, -584, -3.93, -99, 4.31);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 5, 5.3, -384, 0.58, 19, -40.585);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', -40, 5.185, 6, -98.8, -428, -821.77);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -73, 4.1, 4, -1.650, -50, 41.56, -346);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -73, 4.1, 4, -1.650, -50, 41.56);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -7, 89.81, 4, -800.1, 64, 345.936);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -7, 89.81, 4, -800.1, 64, 345.936);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -37, 485.81, 33, 701.66, 48, 405.775);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -37, 485.81, 33, 701.66, 48, 405.775);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -6, -583.2, 2, -0.9, 62, 17.0);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', -6, -583.2, 2, -0.9, 62, 17.0);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 820, -9.583, -690, 7.5, -39, -895.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 820, -9.583, -690, 7.5, -39, -895.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 92, 726.997, 58, 21.07, 46, 36.74);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 868, 545.26, -7, -491.20, 458, -755.19);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 1, 0.021, 35, -3.907, -3, 28.170);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 78, -41.5, -264, 8.961, 30, 27.0);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 62, 243.29, -664, -133.886, 61, 1.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -9, -8.034, -9, -38.83, -834, -64.0, 897);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -9, -8.034, -9, -38.83, -834, -64.0);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -25, -31.4, 32, -93.4, -3, 43.734);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -25, -31.4, 32, -93.4, -3, 43.734);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', 9, 9.37, 427, -3.756, 76, -0.946);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', 9, 9.37, 427, -3.756, 76, -0.946);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -3, -942.2, 5, -528.26, -727, -341.986);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -3, -942.2, 5, -528.26, -727, -341.986);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -52, 18.76, -4, -0.66, 97, -73.411);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -52, 18.76, -4, -0.66, 97, -73.411);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -7, -291.50, -22, 346.17, -9, -8.75);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -131, 40.12, 0, 653.3, 82, 6.1);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -942, 52.117, 622, 805.558, -7, 337.04);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', 80, 3.727, -7, 65.397, -70, 11.13);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', -531, 8.6, -644, -38.6, -329, 4.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 0, 0.1, 6, -1.45, 34, -68.7, -912);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 0, 0.1, 6, -1.45, 34, -68.7);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -757, -42.254, -2, 9.10, 6, 396.59);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -757, -42.254, -2, 9.10, 6, 396.59);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -1, -2.9, -319, -3.255, -67, 37.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -1, -2.9, -319, -3.255, -67, 37.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 7, -805.881, -52, 3.714, -51, -258.34);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 7, -805.881, -52, 3.714, -51, -258.34);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -561, -5.13, -989, -61.6, -83, 2.5);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -561, -5.13, -989, -61.6, -83, 2.5);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -2, -183.224, 458, -97.60, 408, 212.933);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 89, 20.8, -820, 682.20, 66, -5.98);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', -920, -514.826, -74, 7.1, 43, 26.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 7, -169.2, -124, -174.083, 57, 701.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 0, 15.66, 2, 257.60, -958, 907.37);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 8, -738.0, -6, -11.09, -754, 48.3, 432);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 8, -738.0, -6, -11.09, -754, 48.3);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 201, -482.4, 47, -36.11, 78, 11.214);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 201, -482.4, 47, -36.11, 78, 11.214);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 971, -546.2, 46, 558.12, 0, 443.318);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 971, -546.2, 46, 558.12, 0, 443.318);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 383, 7.0, -9, 81.2, -78, 1.17);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 383, 7.0, -9, 81.2, -78, 1.17);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 97, -2.24, 555, 6.7, 49, 5.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 97, -2.24, 555, 6.7, 49, 5.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 269, -99.8, -327, -3.9, 40, -1.259);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 687, 0.067, 7, 1.6, -19, 5.017);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', -13, -5.593, -4, -5.03, 93, -5.11);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', -5, -385.5, 1, -75.826, -792, 788.396);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', -231, -2.2, 2, 310.47, 9, -90.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 70, 793.923, 734, 73.0, -58, -442.94, 752);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 70, 793.923, 734, 73.0, -58, -442.94);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -632, -8.1, 950, -645.6, -999, 64.9);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -632, -8.1, 950, -645.6, -999, 64.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -63, 5.5, -23, 164.084, 348, 6.117);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -63, 5.5, -23, 164.084, 348, 6.117);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -45, -7.9, 2, -336.1, 4, 266.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -45, -7.9, 2, -336.1, 4, 266.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -9, -652.5, -6, 614.6, -21, 472.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -9, -652.5, -6, 614.6, -21, 472.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -99, -494.7, 850, -1.675, 57, -376.9);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 69, 95.2, 1, -96.004, -471, 99.4);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -53, -215.15, 8, 464.47, 845, 852.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 76, -90.8, 4, 92.533, -829, -89.19);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', -795, -6.060, -589, -34.6, -6, 106.30);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -73, -717.930, -15, 743.88, 63, 50.773, -589);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -73, -717.930, -15, 743.88, 63, 50.773);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -338, 40.06, -209, 314.215, 77, -64.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -338, 40.06, -209, 314.215, 77, -64.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -489, 76.38, 14, -745.421, 2, 9.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -489, 76.38, 14, -745.421, 2, 9.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -64, 3.6, 565, 152.19, -990, -6.11);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -64, 3.6, 565, 152.19, -990, -6.11);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 0, -3.311, 4, 7.255, 0, -185.36);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 0, -3.311, 4, 7.255, 0, -185.36);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 8, -127.1, 9, -4.645, -4, 604.694);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', -102, -93.6, 7, -33.600, 720, 72.5);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 780, 81.59, -249, -46.8, -174, 59.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 4, -1.511, 0, 88.00, 78, 700.55);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 371, -7.8, 7, 5.09, 9, -7.181);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 69, 3.2, -8, -51.95, -970, -0.6, -405);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 69, 3.2, -8, -51.95, -970, -0.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', -3, 9.1, -82, 8.8, -8, -65.58);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', -3, 9.1, -82, 8.8, -8, -65.58);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 97, 32.071, -9, 0.35, 76, -74.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 97, 32.071, -9, 0.35, 76, -74.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 21, -41.221, -98, 297.330, 433, 30.046);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 21, -41.221, -98, 297.330, 433, 30.046);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 74, 533.8, 14, -406.5, -31, -155.3);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 74, 533.8, 14, -406.5, -31, -155.3);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 671, -8.9, 6, 111.610, 5, -886.637);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 680, -29.34, 65, 319.0, -38, 0.045);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', -18, 508.85, -52, -8.51, -61, -868.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 75, -23.9, 4, 559.016, 943, 9.0);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 4, -138.55, 422, 3.86, 8, 703.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', -12, 392.652, 9, 8.4, 9, 472.1, 436);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', -12, 392.652, 9, 8.4, 9, 472.1);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 937, -20.636, 523, 0.102, -920, 5.509);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 937, -20.636, 523, 0.102, -920, 5.509);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 61, 6.942, 1, -3.455, -410, -3.725);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 61, 6.942, 1, -3.455, -410, -3.725);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 7, 4.536, 33, 98.6, 497, -913.004);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 7, 4.536, 33, 98.6, 497, -913.004);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 0, 18.6, -74, 931.92, 0, -6.7);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 0, 18.6, -74, 931.92, 0, -6.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 6, -21.91, 692, -90.7, 3, -69.76);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', -450, 83.90, 5, 7.13, 3, 7.98);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', -6, -5.8, 478, -5.9, 321, 40.88);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', -2, -296.73, 523, -28.09, -7, -315.210);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 63, -88.27, -2, -1.3, -1, 534.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 19, -12.475, -633, -4.97, -5, 184.39, -456);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 19, -12.475, -633, -4.97, -5, 184.39);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -9, -882.04, -99, -1.45, -96, 9.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -9, -882.04, -99, -1.45, -96, 9.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -443, 7.405, -988, -46.7, -208, 25.60);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -443, 7.405, -988, -46.7, -208, 25.60);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -507, -3.877, -76, -38.887, -8, 278.392);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -507, -3.877, -76, -38.887, -8, 278.392);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -46, 6.007, 1, 631.5, 157, 1.70);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -46, 6.007, 1, 631.5, 157, 1.70);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -59, -7.6, 6, -575.730, -27, -7.98);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 676, 8.131, 7, -8.657, -858, -56.16);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 561, 4.092, 54, 2.203, -39, 338.73);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -7, -340.163, -780, -26.2, 16, 784.76);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', -97, -70.39, 94, 1.0, -249, 5.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -332, 243.457, -26, -2.4, -465, 431.42, -933);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -332, 243.457, -26, -2.4, -465, 431.42);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -638, -211.7, 5, -491.30, -12, -435.930);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -638, -211.7, 5, -491.30, -12, -435.930);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 5, 3.652, -79, -12.557, -8, 7.907);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 5, 3.652, -79, -12.557, -8, 7.907);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 55, -933.6, -4, 99.738, -86, -6.3);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 55, -933.6, -4, 99.738, -86, -6.3);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 88, 0.33, -14, -524.99, -438, -28.266);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 88, 0.33, -14, -524.99, -438, -28.266);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -904, -84.078, 762, 513.80, 68, -4.50);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -5, 624.05, -2, 8.439, -243, 55.3);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -70, -976.76, 44, -2.673, -730, 366.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 469, 500.515, 211, -786.738, 6, 181.9);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', -18, 78.2, -8, -42.788, 3, 80.505);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -58, -569.8, -2, -98.79, -942, 461.54, 856);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -58, -569.8, -2, -98.79, -942, 461.54);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -974, 30.1, 76, 34.0, -898, -338.755);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -974, 30.1, 76, 34.0, -898, -338.755);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 7, 9.4, 33, -96.4, -321, -99.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 7, 9.4, 33, -96.4, -321, -99.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 5, 824.057, 50, -893.73, -80, -72.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 5, 824.057, 50, -893.73, -80, -72.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 23, 242.189, 5, -90.6, -940, 158.871);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 23, 242.189, 5, -90.6, -940, 158.871);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -84, 4.79, -424, 593.23, -9, 1.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -4, -72.457, 40, -35.765, 85, -86.20);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -3, -93.0, 2, -415.549, 6, -3.09);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -25, -6.25, -57, 8.3, -123, -462.64);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', -4, -613.2, 9, -139.990, 59, 119.460);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 969, 411.171, 8, -44.896, -159, 90.196, 652);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 969, 411.171, 8, -44.896, -159, 90.196);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 554, 869.12, 8, -75.20, -7, 954.414);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 554, 869.12, 8, -75.20, -7, 954.414);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 61, 589.32, 120, -1.628, 8, -164.195);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 61, 589.32, 120, -1.628, 8, -164.195);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -2, -3.7, 791, 664.8, -808, -25.64);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -2, -3.7, 791, 664.8, -808, -25.64);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -1, -356.619, -7, 1.363, 33, 86.1);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -1, -356.619, -7, 1.363, 33, 86.1);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -5, 97.81, -698, 96.926, 80, -9.014);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -7, 96.8, 47, 447.8, 0, 31.13);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 974, 9.0, 4, 9.1, 0, 8.400);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -1, -37.09, -616, 61.481, 60, 45.625);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', -358, -113.743, 111, -54.958, -1, 7.3);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 0, 15.1, 795, -637.29, -58, -9.691, 247);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 0, 15.1, 795, -637.29, -58, -9.691);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -238, -4.19, 2, 759.1, 35, 1.785);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -238, -4.19, 2, 759.1, 35, 1.785);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 938, -9.26, -89, 778.270, -585, -1.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 938, -9.26, -89, 778.270, -585, -1.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 2, -423.241, 70, -3.093, 604, 8.9);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 2, -423.241, 70, -3.093, 604, 8.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -95, -680.6, 8, 8.39, 6, 9.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -95, -680.6, 8, 8.39, 6, 9.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 0, 1.92, 7, -98.3, 33, 973.880);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -161, 592.73, -6, 36.578, 90, 5.972);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', -97, -51.48, -21, 16.37, -56, 7.430);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 9, -25.615, -7, -17.26, -7, -3.30);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 733, -8.14, -52, -36.1, 732, 7.564);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 2, 72.8, -3, 370.94, -398, 203.8, 980);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 2, 72.8, -3, 370.94, -398, 203.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 13, -5.36, 11, 669.306, 880, -3.70);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 13, -5.36, 11, 669.306, 880, -3.70);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -2, -2.812, 0, -5.0, 7, -12.66);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -2, -2.812, 0, -5.0, 7, -12.66);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -7, -2.932, 550, -805.29, 8, -113.41);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -7, -2.932, 550, -805.29, 8, -113.41);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -6, -566.119, -6, 8.594, 730, -502.13);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -6, -566.119, -6, 8.594, 730, -502.13);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 40, -583.9, 645, -91.70, 3, 30.358);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -41, 6.825, -8, 31.389, 9, 496.256);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 6, 68.0, -20, -655.0, -8, -1.5);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', -8, 5.4, 76, 557.042, 0, -32.9);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 555, 386.64, 233, 45.63, 4, 765.4);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 4, 9.239, -81, 112.036, -3, -15.25, 81);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 4, 9.239, -81, 112.036, -3, -15.25);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -5, -7.2, 911, 92.35, 7, -0.22);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -5, -7.2, 911, 92.35, 7, -0.22);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -72, -9.15, -9, 319.00, -60, 6.288);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -72, -9.15, -9, 319.00, -60, 6.288);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -1, 3.527, 892, 78.827, 5, 6.452);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -1, 3.527, 892, 78.827, 5, 6.452);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 2, -624.5, -36, 278.67, 2, 17.15);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 2, -624.5, -36, 278.67, 2, 17.15);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -644, 7.1, 5, 58.623, 4, -2.0);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 696, 32.329, 11, 9.96, -93, -31.9);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 38, 57.810, 1, -55.3, 81, 4.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', -743, 221.34, 749, -10.16, 438, 177.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 1, 2.144, -45, 336.1, -7, -97.46);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 8, 2.2, -403, -5.4, -371, 82.9, -431);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 8, 2.2, -403, -5.4, -371, 82.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -2, -2.71, 80, -47.4, -1, 2.3);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -2, -2.71, 80, -47.4, -1, 2.3);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -84, -2.029, 8, -823.6, 1, 431.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -84, -2.029, 8, -823.6, 1, 431.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 93, -610.9, -9, 172.5, 7, 15.666);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 93, -610.9, -9, 172.5, 7, 15.666);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -2, -842.859, 4, -1.58, -7, 7.38);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -2, -842.859, 4, -1.58, -7, 7.38);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -45, 447.5, 7, -325.12, 818, 7.240);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -30, 7.7, 519, 0.236, 8, -45.3);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 775, -489.665, 9, -268.02, -17, 26.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 5, -85.60, 9, 64.45, -66, 2.4);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', -7, -96.240, 5, -449.096, -3, -40.60);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -8, -30.97, -67, 63.98, 56, -88.5, 222);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -8, -30.97, -67, 63.98, 56, -88.5);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 85, 87.549, 824, 5.30, 211, 3.93);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 85, 87.549, 824, 5.30, 211, 3.93);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -423, 33.579, -8, 235.747, 0, 17.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -423, 33.579, -8, 235.747, 0, 17.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 0, 23.88, 0, -694.0, -4, -96.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 0, 23.88, 0, -694.0, -4, -96.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -579, 35.51, 8, 41.25, 17, -356.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -579, 35.51, 8, 41.25, 17, -356.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -5, -13.98, 864, -4.604, 0, -4.36);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', -3, -464.73, 6, 1.3, -277, 4.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 65, 1.369, -596, 9.03, 3, -3.31);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 100, -329.482, 8, -925.4, 2, -8.565);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 28, -521.42, -8, 0.165, -53, 6.234);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 1, 3.5, -7, -65.544, -64, -50.94, -244);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 1, 3.5, -7, -65.544, -64, -50.94);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 581, 48.00, 34, 191.221, -992, -288.05);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 581, 48.00, 34, 191.221, -992, -288.05);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 0, -3.4, 6, 12.66, 9, -38.7);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 0, -3.4, 6, 12.66, 9, -38.7);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -713, -29.0, -26, 1.99, 495, -7.946);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -713, -29.0, -26, 1.99, 495, -7.946);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -909, 3.60, -1, 6.65, -42, -972.3);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -909, 3.60, -1, 6.65, -42, -972.3);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 437, -94.4, -1, 699.6, -8, -334.1);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -37, -900.216, 4, -93.14, 807, -44.455);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 2, -7.3, 18, 607.95, 4, 119.06);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 5, -424.16, -214, -772.972, 0, 19.13);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', -9, -86.76, 711, -2.86, 98, -85.34);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -7, 7.30, -533, -615.389, 625, 8.305, 619);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -7, 7.30, -533, -615.389, 625, 8.305);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 5, -3.84, 733, -765.73, 265, -14.175);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 5, -3.84, 733, -765.73, 265, -14.175);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 954, -0.02, 54, 0.13, -609, 9.603);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 954, -0.02, 54, 0.13, -609, 9.603);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -141, 3.001, -857, 5.79, -5, -66.01);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -141, 3.001, -857, 5.79, -5, -66.01);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 0, 832.865, -450, 431.6, -11, 525.540);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 0, 832.865, -450, 431.6, -11, 525.540);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -46, -359.551, 5, -74.33, 14, -680.41);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 9, -9.078, 17, 8.15, 3, 97.753);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', -63, 7.596, -3, -0.97, -2, 450.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 9, -91.8, -488, -455.518, -1, 2.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 25, -1.215, 23, -12.4, -671, 28.95);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 888, 875.57, 3, 4.5, -61, 43.349, -426);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 888, 875.57, 3, 4.5, -61, 43.349);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 344, -58.87, 860, 82.82, -66, -0.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 344, -58.87, 860, 82.82, -66, -0.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -6, -9.0, 0, 896.2, -248, -11.864);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -6, -9.0, 0, 896.2, -248, -11.864);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -402, 3.8, 25, -906.65, -29, 3.20);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -402, 3.8, 25, -906.65, -29, 3.20);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 546, -3.22, 91, 21.724, -110, 61.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 546, -3.22, 91, 21.724, -110, 61.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 0, -4.37, -3, 648.0, -9, 529.504);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -108, 487.69, -472, -58.4, -426, -773.120);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -207, -27.211, -850, -9.8, 95, -94.667);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 7, 216.53, 1, 996.0, 56, -8.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', -5, -42.289, -8, 19.545, 6, -99.387);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -98, -4.477, -156, 80.3, -98, -5.9, -261);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -98, -4.477, -156, 80.3, -98, -5.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -811, -5.32, -469, -92.062, -1, -849.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -811, -5.32, -469, -92.062, -1, -849.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -79, -3.30, 634, -631.2, 1, -192.6);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -79, -3.30, 634, -631.2, 1, -192.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 25, 59.4, -593, 22.2, -807, 5.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 25, 59.4, -593, 22.2, -807, 5.2);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 42, 778.10, 0, -66.84, -531, 95.92);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 42, 778.10, 0, -66.84, -531, 95.92);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 342, -60.18, -2, 701.50, 93, 6.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -91, -78.228, -9, 19.510, 8, -990.72);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -8, 5.672, -6, -53.56, 0, 397.7);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 41, 7.750, -5, -829.16, -777, -9.816);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', -250, 19.140, 0, 24.7, 8, 6.9);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 907, -800.2, 50, 5.9, 913, -2.284, -327);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 907, -800.2, 50, 5.9, 913, -2.284);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -14, -6.89, 37, -8.15, -98, -490.287);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -14, -6.89, 37, -8.15, -98, -490.287);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 3, 336.367, 45, 8.7, -693, 893.81);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 3, 336.367, 45, 8.7, -693, 893.81);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 331, 47.3, 12, -324.10, 7, 390.17);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 331, 47.3, 12, -324.10, 7, 390.17);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -63, 869.79, -6, -4.41, -1, -593.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -63, 869.79, -6, -4.41, -1, -593.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 74, -8.1, 515, -338.13, 4, -6.30);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 29, 957.93, 34, 34.52, -368, -110.6);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 15, -8.78, 99, -222.1, -80, -830.23);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 64, -46.4, 788, 4.1, -19, 1.91);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', 0, 86.0, 720, 9.021, -9, -174.898);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 1, 836.715, 27, 63.593, 11, -947.8, 765);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 1, 836.715, 27, 63.593, 11, -947.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 773, 896.991, -96, -837.0, -3, -9.867);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 773, 896.991, -96, -837.0, -3, -9.867);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 29, -566.48, 18, -2.76, -62, 2.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 29, -566.48, 18, -2.76, -62, 2.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -707, 18.8, 65, 980.32, -9, 2.375);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -707, 18.8, 65, 980.32, -9, 2.375);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 54, 721.496, -656, 53.4, -428, -4.92);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 54, 721.496, -656, 53.4, -428, -4.92);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 8, -4.517, -86, 171.250, -3, -8.951);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', 576, 8.9, -132, 823.68, -40, -104.356);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -709, -5.061, 98, -60.899, -16, 6.21);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -62, -6.1, 46, 3.60, -620, 5.390);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -792, 490.54, -900, -3.61, 989, 79.491);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -803, 756.435, -90, -162.0, 164, -776.0, 882);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -803, 756.435, -90, -162.0, 164, -776.0);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 143, -7.669, 51, 96.50, 1, -658.1);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 143, -7.669, 51, 96.50, 1, -658.1);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -2, -53.7, 996, 52.42, 7, 8.872);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -2, -53.7, 996, 52.42, 7, 8.872);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 63, -498.24, 8, 4.5, 44, 86.23);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 63, -498.24, 8, 4.5, 44, 86.23);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -7, -1.6, -12, 455.490, 4, -4.2);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -7, -1.6, -12, 455.490, 4, -4.2);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -5, -732.785, 19, 62.350, -96, 8.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -111, -3.0, 842, 83.184, -898, -512.844);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -9, 6.7, 419, 1.53, -50, 37.17);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 86, 802.2, 40, -683.42, -815, -814.398);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', 5, -336.3, 5, 817.33, -4, -781.46);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -57, -57.69, -46, -1.83, 27, -2.51, 420);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -57, -57.69, -46, -1.83, 27, -2.51);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', 238, -5.037, -584, 726.539, 288, -609.61);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', 238, -5.037, -584, 726.539, 288, -609.61);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -2, 5.28, 83, 65.31, 47, -7.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -2, 5.28, 83, 65.31, 47, -7.8);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', 57, -284.1, -67, 9.24, -6, 189.396);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', 57, -284.1, -67, 9.24, -6, 189.396);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -761, 984.832, -223, -7.1, 74, -4.091);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -761, 984.832, -223, -7.1, 74, -4.091);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -620, -79.932, -9, -9.78, 5, 0.10);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -7, -940.828, 9, 112.6, 4, -144.315);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -7, 883.32, 0, -10.587, 3, 76.037);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -865, 674.43, -8, 84.51, 4, -583.001);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -8, 3.16, 2, -47.009, -75, -38.93);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3, sum_integer_join4)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -636, 74.80, 541, 52.3, 6, 79.6, -602);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641945600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -636, 74.80, 541, 52.3, 6, 79.6);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 482, 3.16, 15, -1.7, 1, -762.119);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641949200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 482, 3.16, 15, -1.7, 1, -762.119);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -85, -3.85, 33, -70.701, -7, -329.54);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641952800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -85, -3.85, 33, -70.701, -7, -329.54);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 956, 76.674, -7, 172.8, 9, -111.05);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641956400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 956, 76.674, -7, 172.8, 9, -111.05);
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -186, 845.6, 145, -2.35, 8, -70.8);
INSERT INTO kpi.kpi_cell_guid_60 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1641960000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -186, 845.6, 145, -2.35, 8, -70.8);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658102400 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 0, -582.9, 4, -9.800, -2, -69.685);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658188800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -50, -641.3, 736, 233.702, 733, -26.5);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658275200 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -45, -2.51, -20, -4.904, -483, -5.3);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658361600 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 5, -80.8, 24, -697.4, 841, -296.681);
INSERT INTO kpi.kpi_cell_sector_1440 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_join1, sum_float_join1, sum_integer_join2, sum_float_join2, sum_integer_join3, sum_float_join3)
VALUES (to_timestamp(cast(1658448000 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', 2, -4.82, 7, 8.1, 4, 6.1);
INSERT INTO kpi.kpi_cell_sector_15 (aggregation_begin_time, agg_column_1, agg_column_2, agg_column_3, sum_integer_exportable, sum_float_exportable, sum_integer_not_exportable, sum_float_not_exportable)
VALUES (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 2, -44.82, 708, 8.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 25, -24.82, 234, 18.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', 32, 4.82, 17, 8.19),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 22, 24.82, -7, 98.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 14, 34.82, -677, -8.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 708, 14.82, -237, -83.19),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 12, -43.82, -17, 88.81),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 34, 4.82, 71, 12.13),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 55, -4.8, 74, -33.41),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 44, -4.2, -205, -70.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 1, -604.82, 66, 78.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 2, -544.82, 798, 83.51),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 3, 94.28, -657, -3.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 65, -4.82, 37, 12.12),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 408, -4.82, 74, 33.79),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 420, -4.2, 69, 6.9),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 290, -4.08, 52, -6.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 245, 54.98, 19, -40.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 223, 46.44, -205, -128.15),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 122, 49.09, 333, 8.66),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 123, 904.82, 123, 3.99),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 526, 4.82, -124, -102.4),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -2, -4.82, 15, 6.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -602, 33.24, 107, 12.41),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -342, -12.34, -73, 158.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -245, -304.19, 72, 38.1),
       (to_timestamp(cast(1678060800 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -252, 402.42, 99, 8.61),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue1', 'dimValue1', 2, -44.82, 708, 8.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue1', 'dimValue2', 25, -24.82, 234, 18.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue1', 'dimValue3', 32, 4.82, 17, 8.19),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue2', 'dimValue1', 22, 24.82, -7, 98.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue2', 'dimValue2', 14, 34.82, -677, -8.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue2', 'dimValue3', 708, 14.82, -237, -83.19),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue3', 'dimValue1', 12, -43.82, -17, 88.81),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue3', 'dimValue2', 34, 4.82, 71, 12.13),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue1', 'dimValue3', 'dimValue3', 55, -4.8, 74, -33.41),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue1', 'dimValue1', 44, -4.2, -205, -70.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue1', 'dimValue2', 1, -604.82, 66, 78.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue1', 'dimValue3', 2, -544.82, 798, 83.51),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue2', 'dimValue1', 3, 94.28, -657, -3.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue2', 'dimValue2', 65, -4.82, 37, 12.12),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue2', 'dimValue3', 408, -4.82, 74, 33.79),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue3', 'dimValue1', 420, -4.2, 69, 6.9),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue3', 'dimValue2', 290, -4.08, 52, -6.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue2', 'dimValue3', 'dimValue3', 245, 54.98, 19, -40.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue1', 'dimValue1', 223, 46.44, -205, -128.15),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue1', 'dimValue2', 122, 49.09, 333, 8.66),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue1', 'dimValue3', 123, 904.82, 123, 3.99),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue2', 'dimValue1', 526, 4.82, -124, -102.4),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue2', 'dimValue2', -2, -4.82, 15, 6.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue2', 'dimValue3', -602, 33.24, 107, 12.41),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue3', 'dimValue1', -342, -12.34, -73, 158.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue3', 'dimValue2', -245, -304.19, 72, 38.1),
       (to_timestamp(cast(1678061700 as bigint)), 'dimValue3', 'dimValue3', 'dimValue3', -252, 402.42, 99, 8.61);