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

package com.ericsson.oss.air.pm.stats.exporter.runner;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Application runner to check necessary topics and wait for inner state to restore.
 */
@Component
@ConditionalOnProperty({"kafka.enabled", "datasource.enabled"})
public class ExporterApplicationRunner implements ApplicationRunner  {

    @Autowired
    private ExporterDependencyChecker exporterDependencyChecker;

    /**
     * Initializer method, to check, that the necessary topics, for the application start are there,
     * and to wait for the inner state to restore normally (if this isn't the first time starting up).
     * This method is automatically started by Spring, after the initialization of the ApplicationContext is finished.
     *
     * @param args not used, but provided by Spring by default
     */
    @Override
    public void run(final ApplicationArguments args) throws SQLException {
        exporterDependencyChecker.checkDependencies();
    }

}
