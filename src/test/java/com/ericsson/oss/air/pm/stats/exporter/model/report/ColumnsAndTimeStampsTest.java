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

package com.ericsson.oss.air.pm.stats.exporter.model.report;

import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.AGGREGATION_BEGIN_TIME;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.EXECUTION_ID;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.DIM2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI1;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI2;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI3;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI4;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.KPI_CELL_SECTOR_60;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.SCHEDULED;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_12_00_00_S;
import static com.ericsson.oss.air.pm.stats.exporter.helpers.utils.TestConstants.TS_2022_05_17_13_00_00_S;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class ColumnsAndTimeStampsTest {
    private final List<String> dimensions = List.of(AGGREGATION_BEGIN_TIME, DIM1, DIM2);
    private final List<String> kpis = List.of(KPI1, KPI2, KPI3, KPI4);

    @Test
    void whenGetColumnsCalled_shouldGiveBackAllKpiAndDimensionColumns() {
        final ColumnsAndTimeStamps model = new ColumnsAndTimeStamps(dimensions, kpis, SCHEDULED,
                TS_2022_05_17_12_00_00_S, TS_2022_05_17_13_00_00_S, KPI_CELL_SECTOR_60, EXECUTION_ID);
        assertThat(model.getColumns())
                .containsAll(dimensions)
                .containsAll(kpis);
    }
}