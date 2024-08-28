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

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("kafkaTest")
@SpringBootTest(classes = ExporterApplicationRunner.class)
public class ExporterApplicationRunnerTest {

    @MockBean
    private ExporterDependencyChecker exporterDependencyCheckerMock;

    @Test
    void whenApplicationRun_shouldExporterDependenciesBeChecked() {
        verify(exporterDependencyCheckerMock).checkDependencies();
    }
}
