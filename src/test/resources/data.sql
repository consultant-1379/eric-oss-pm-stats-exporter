--
-- COPYRIGHT Ericsson 2022
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
--  1652785200:	2022-05-17 11:00:00
--  1652788800:	2022-05-17 12:00:00
--  1652792400:	2022-05-17 13:00:00

set time zone UTC;

CREATE SCHEMA IF NOT EXISTS kpi;

CREATE TABLE IF NOT EXISTS kpi.kpi_cell_sector_60 (
  aggregation_begin_time TIMESTAMP NOT NULL,
  dim1 BIGINT,
  dim2 VARCHAR(255),
  kpi1 BIGINT,
  kpi2 DOUBLE PRECISION,
  kpi3 VARCHAR(64)
);

INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652785200 as bigint)), 1, 'dimValue1', 877628525, 45450.2049, '331290781');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652785200 as bigint)), 2, 'dimValue2', 260129625, 9632.73418, '779838901');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652785200 as bigint)), 3, 'dimValue3', 434687883, 37793.0466, '831673640');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652788800 as bigint)), 4, 'dimValue1', 966608473, 7818.5965, '27476439');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652788800 as bigint)), 5, 'dimValue2', 1113764514, 10106.97172, '182377383');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652788800 as bigint)), 6, 'dimValue3', 369929604, 33247.8488, '827534987');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652792400 as bigint)), 7, 'dimValue1', 382543116, 3851.26430, '798644295');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652792400 as bigint)), 8, 'dimValue2', 1163180048, 110961.5199, '318472194');
INSERT INTO kpi.kpi_cell_sector_60 (aggregation_begin_time, dim1, dim2, kpi1, kpi2, kpi3) VALUES (to_timestamp(cast(1652792400 as bigint)), 9, 'dimValue3', 588769527, 7089.20626, '150739783');


CREATE TABLE IF NOT EXISTS kpi.kpi_cell_sector_1440 (
  aggregation_begin_time TIMESTAMP NOT NULL,
  dim1 BIGINT,
  dim2 VARCHAR(255),
  kpi1 BIGINT,
  kpi2 DOUBLE PRECISION,
  kpi3 VARCHAR(64)
);
