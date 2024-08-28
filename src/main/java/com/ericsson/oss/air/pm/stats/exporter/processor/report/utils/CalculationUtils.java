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

package com.ericsson.oss.air.pm.stats.exporter.processor.report.utils;

import static java.lang.Long.MAX_VALUE;
import static java.lang.Long.MIN_VALUE;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.LongBinaryOperator;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;

import com.ericsson.oss.air.pm.stats.exporter.model.api.Kpi;
import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.KpiState;
import com.ericsson.oss.air.pm.stats.exporter.model.report.ExecutionReportKpi;

/**
 * Utility class to collect all the calculations related to the {@link Kpi} interface.
 */
public final class CalculationUtils {
    private static final Predicate<Kpi> NOT_NULL = Objects::nonNull;
    private static final Predicate<Kpi> IS_EXPORTABLE = Kpi::isExportable;
    private static final Predicate<Kpi> REEXPORT_LATE_DATA = Kpi::isReexportLateDataEnabled;
    private static final Predicate<Kpi> START_TIME_NOT_NULL = kpi -> kpi.longCalculationStartTime() != null;

    private static final ToLongFunction<Kpi> GET_RELIABILITY = Kpi::longReliabilityThreshold;
    private static final ToLongFunction<Kpi> GET_START_TIME = Kpi::longCalculationStartTime;

    private static final LongBinaryOperator SMALLER_THAN = (baseValue, newValue) -> newValue < baseValue ? newValue : baseValue;
    private static final LongBinaryOperator GREATER_THAN = (baseValue, newValue) -> newValue > baseValue ? newValue : baseValue;

    private CalculationUtils() {
    }

    /**
     * The method checks through all the Kpis where reexport late data is true and exportable, and
     * determines the smallest time.
     * @param kpiList list of kpis to check
     * @return the lowest calculation start time
     */
    public static Long calculateStartTimeForLateData(final List<ExecutionReportKpi> kpiList) {
        return calculateMinOrMaxForGivenAttribute(
            kpiList.stream()
                .map(Kpi::of)
                .collect(Collectors.toList()),
                SMALLER_THAN, IS_EXPORTABLE.and(REEXPORT_LATE_DATA), GET_START_TIME, MAX_VALUE, MAX_VALUE);
    }

    /**
     * Selects the smallest start time from the given set of Kpis, if any is present.
     * If none of the Kpis have start time, then selects the smallest reliability
     * @param kpiSet list of kpis to calculate from
     * @param aggregationPeriod for calculating reliability
     * @return {@link Long} with the smallest start time, or smallest reliability
     */
    public static String calculateStartTimeForGenericKpi(final Set<Entry<String, KpiState>> kpiSet, final Integer aggregationPeriod) {
        final Long fallBackValue = calculateMinOrMaxForGivenAttribute(
                kpiSet.stream()
                    .map(Entry::getValue)
                    .map(Kpi::of)
                    .collect(Collectors.toList()),
                SMALLER_THAN, NOT_NULL, GET_RELIABILITY, MAX_VALUE, MAX_VALUE) - aggregationPeriod * 60;
        return String.valueOf(calculateMinOrMaxForGivenAttribute(
            kpiSet.stream()
                .map(Entry::getValue)
                .map(Kpi::of)
                .collect(Collectors.toList()),
                SMALLER_THAN, NOT_NULL.and(START_TIME_NOT_NULL), GET_START_TIME, fallBackValue, MAX_VALUE));
    }

    /**
     * The method checks through all the Kpis which is present and exportable
     * to determine the smallest common reliability.
     * @param kpiSet list of kpis to calculate from
     * @return the smallest common reliability where the Kpi is exportable or 0 if
     *         there is any null kpi or only not exportable Kpi.
     */
    public static Long calculateSmallestReliability(final Set<Entry<String, KpiState>> kpiSet) {
        return kpiSet.stream()
            .filter(stringKpiStateEntry -> stringKpiStateEntry.getValue() == null)
            .mapToLong(kpi -> 0L).findFirst()
            .orElseGet(() -> calculateMinOrMaxForGivenAttribute(
                kpiSet.stream()
                    .map(Entry::getValue)
                    .map(Kpi::of)
                    .collect(Collectors.toList()),
                    SMALLER_THAN, NOT_NULL.and(IS_EXPORTABLE), GET_RELIABILITY, 0L, MAX_VALUE));
    }

    /**
     * The method checks through all the Kpis which is present and exportable
     * to determine the greatest reliability threshold.
     * @param kpiSet list of kpis to calculate from
     * @return the greatest reliability where the Kpi is exportable.
     */
    public static Long calculateGreatestReliability(final Set<Entry<String, KpiState>> kpiSet) {
        return calculateMinOrMaxForGivenAttribute(
            kpiSet.stream()
                .map(Entry::getValue)
                .map(Kpi::of)
                .collect(Collectors.toList()),
                GREATER_THAN, NOT_NULL.and(IS_EXPORTABLE), GET_RELIABILITY, MIN_VALUE, MIN_VALUE);
    }

    /**
     * The method checks through all the Kpis which is present and exportable
     * to determine the smallest calculation time.
     * @param kpiSet list of kpis to calculate from
     * @return the smallest calculation time, where the Kpi is exportable.
     */
    public static Long calculateSmallestCalculationTime(final Set<Entry<String, KpiState>> kpiSet) {
        return calculateMinOrMaxForGivenAttribute(
            kpiSet.stream()
                .map(Entry::getValue)
                .map(Kpi::of)
                .collect(Collectors.toList()),
                SMALLER_THAN, NOT_NULL.and(IS_EXPORTABLE), GET_START_TIME, MAX_VALUE, MAX_VALUE);
    }

    private static Long calculateMinOrMaxForGivenAttribute(
            final List<Kpi> kpisEntrySet,
            final LongBinaryOperator longBinaryOperator,
            final Predicate<Kpi> filterCondition,
            final ToLongFunction<Kpi> getLongAttribute,
            final Long fallbackValue,
            final Long baseValue
    ) {
        final Long result = kpisEntrySet.stream()
                .filter(filterCondition)
                .mapToLong(getLongAttribute)
                .reduce(baseValue, longBinaryOperator);
        return result.equals(baseValue) ? fallbackValue : result;
    }
}
