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

package com.ericsson.oss.air.pm.stats.exporter.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ericsson.oss.air.pm.stats.exporter.model.datacatalog.exception.DataCatalogException;

/**
 * Class to determine the availability of the Data Catalog.
 */
@Component("data-catalog")
@ConditionalOnProperty("dc.enabled")
public class DataCatalogHealthIndicator implements HealthIndicator {

    @Autowired
    private WebClient webClient;

    /**
     * Method to determine if the Data Catalog is available.
     */
    @Override
    @SuppressWarnings("squid:S1166")
    public Health health() {
        final String dataCatalogActuatorHealthEndPoint = "/actuator/health";
        try {
            webClient.get().uri(dataCatalogActuatorHealthEndPoint)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorMap(DataCatalogException::new)
                    .block();
        } catch (final DataCatalogException e) {
            return Health.down().withDetail("query url", dataCatalogActuatorHealthEndPoint).withDetail("query method", "GET")
                .withDetail("details", e.getMessage()).build();
        }
        return Health.up().withDetail("query url", dataCatalogActuatorHealthEndPoint).withDetail("query method", "GET").build();
    }
}
