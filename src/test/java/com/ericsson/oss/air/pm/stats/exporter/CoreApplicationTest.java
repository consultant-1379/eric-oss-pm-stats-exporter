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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = CoreApplication.class)
@ActiveProfiles("test")
public class CoreApplicationTest {

    @Value("${info.app.description}")
    private String description;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void whenApplicationStarts_shouldMetricsBeAvailable() throws Exception {
        final MvcResult result = mvc.perform(get("/actuator/prometheus").contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .contains("jvm_threads_states_threads");
    }

    @Test
    public void whenApplicationStarts_shouldAppInfoBeAvailable() throws Exception {
        final MvcResult result = mvc.perform(get("/actuator/info").contentType(MediaType.TEXT_PLAIN)).andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .contains(this.description);
    }

    @Test
    public void whenApplicationStarts_shouldSpringContextBeingCreated() {
        try (MockedStatic<SpringApplication> mocked = Mockito.mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(CoreApplication.class, "foo", "bar"))
                    .thenReturn(Mockito.mock(ConfigurableApplicationContext.class));

            CoreApplication.main(new String[] { "foo", "bar" });

            mocked.verify(() -> SpringApplication.run(CoreApplication.class, "foo", "bar"));
        }
    }
}
