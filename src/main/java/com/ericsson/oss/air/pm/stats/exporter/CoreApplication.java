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

package com.ericsson.oss.air.pm.stats.exporter;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.Status;
import com.google.common.base.Stopwatch;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Core Application, the starting point of the application.
 */
@SpringBootApplication
public class CoreApplication {
    /**
     * Main entry point of the application.
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    /**
     * Bean to create the default countDownLatch in ApplicationContext.
     * @return CountDownLatch, initialized with 1
     */
    @Bean
    public CountDownLatch countDownLatch() {
        return new CountDownLatch(1);
    }

    /**
     * Bean to create the default (empty) Status in ApplicationContext.
     * @return empty Status object
     */
    @Bean
    public Status emptyStatus() {
        return new Status();
    }

    /**
     * Bean to create the default timedAspect in ApplicationContext.
     * @param registry MeterRegistry
     * @return TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(final MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Bean to create the stopwatch to measure time of transactions without dependencies in ApplicationContext.
     * @return Stopwatch
     */
    @Bean
    @Qualifier("execution-report_stopwatch")
    public Stopwatch executionReportStopwatch() {
        return Stopwatch.createUnstarted();
    }

    /**
     * Bean to create the stopwatch to measure time of transactions without dependencies in ApplicationContext.
     * @return Stopwatch
     */
    @Bean
    @Qualifier("completed-timestamp_stopwatch")
    public Stopwatch completedTimestampStopwatch() {
        return Stopwatch.createUnstarted();
    }

}