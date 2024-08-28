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

package com.ericsson.oss.air.pm.stats.exporter.health;

import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Class to determine the readiness state of the PM Stats Exporter.
 */
@Component("customReadiness")
public class ReadinessHealthIndicator implements HealthIndicator {

    @Autowired
    private CountDownLatch innerStateRestorationLatch;

    @Value("${kafka.enabled}")
    private boolean kafkaEnabled;

    /**
     * Method to determine if the PM Stats Exporter is in ready state along with all its related components.
     */
    @Override
    public Health health() {
        if (kafkaEnabled) {
            return innerStateRestorationLatch.getCount() == 0
                    ? Health.up().build()
                    : Health.down().withDetail("Kafka", "Waiting for dependant services to be available").build();
        }
        return Health.up().build();
    }
}