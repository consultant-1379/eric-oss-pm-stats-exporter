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

package com.ericsson.oss.air.pm.stats.exporter.event;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.leader.event.OnGrantedEvent;
import org.springframework.integration.leader.event.OnRevokedEvent;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;

import com.ericsson.oss.air.pm.stats.exporter.model.innerstate.exception.InnerStateException;

@ActiveProfiles("kafkaTest")
@SpringBootTest(classes = EventHandler.class)
public class EventHandlerTest {

    private static final String LISTENER_IS_NULL_ERROR = "Container Listener returned with null for listener id: ";
    private static final String INNER_STATE_LISTENER_ID = "innerStateListener";
    private static final String EXECUTION_REPORT_LISTENER_ID = "executionReportListenerId";

    @Autowired
    private EventHandler eventHandler;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Test
    void whenOnGrantedEventReceivedAndListenerContainersExist_shouldNoExceptionsBeThrown() {
        doReturn(mock(MessageListenerContainer.class)).when(kafkaListenerEndpointRegistry).getListenerContainer(anyString());
        assertThatCode(() -> eventHandler.onGrantedEventHandler(mock(OnGrantedEvent.class)))
                .doesNotThrowAnyException();
        verify(kafkaListenerEndpointRegistry, times(3)).getListenerContainer(anyString());
    }

    @Test
    void whenOnGrantedEventReceivedAndListenerContainerIsNull_shouldInnerStateExceptionBeThrown() {
        doReturn(null).when(kafkaListenerEndpointRegistry).getListenerContainer(anyString());
        assertThatThrownBy(() -> eventHandler.onGrantedEventHandler(mock(OnGrantedEvent.class)))
                .isInstanceOf(InnerStateException.class)
                .hasMessage(LISTENER_IS_NULL_ERROR + INNER_STATE_LISTENER_ID);
    }

    @Test
    void whenOnRevokedEventReceivedAndListenerContainersExist_shouldNoExceptionsBeThrown() {
        doReturn(mock(MessageListenerContainer.class)).when(kafkaListenerEndpointRegistry).getListenerContainer(anyString());
        assertThatCode(() -> eventHandler.onRevokedEventHandler(mock(OnRevokedEvent.class)))
                .doesNotThrowAnyException();
        verify(kafkaListenerEndpointRegistry, times(1)).getListenerContainer(anyString());
    }

    @Test
    void whenOnRevokedEventReceivedAndListenerContainerIsNull_shouldInnerStateExceptionBeThrown() {
        doReturn(null).when(kafkaListenerEndpointRegistry).getListenerContainer(anyString());
        assertThatThrownBy(() -> eventHandler.onRevokedEventHandler(mock(OnRevokedEvent.class)))
                .isInstanceOf(InnerStateException.class)
                .hasMessage(LISTENER_IS_NULL_ERROR + EXECUTION_REPORT_LISTENER_ID);
    }
}
