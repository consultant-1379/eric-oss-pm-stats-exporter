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

package com.ericsson.oss.air.pm.stats.exporter.helpers.model;

import java.util.concurrent.TimeoutException;

import org.apache.avro.generic.GenericRecord;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Class implementing the Answer interface. This class is used for simulating an exception during
 * the writing to kafka.
 */
public class ExceptionAnswer implements Answer {
    private static final String TEST_EXCEPTION = "test exception";

    @Override
    public Object answer(final InvocationOnMock invocationOnMock) throws Throwable {
        if ((long) ((GenericRecord) invocationOnMock.getArgument(1)).get("id") == 6) {
            throw new TimeoutException(TEST_EXCEPTION);
        } else {
            return invocationOnMock.callRealMethod();
        }
    }
}
