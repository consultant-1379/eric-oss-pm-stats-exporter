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

package com.ericsson.oss.air.pm.stats.exporter.utils.exception;

/**
 * Class to gracefully handle any exception during receiving messages from Kafka into the Exporter.
 */
public class InvalidJsonMsgException extends Exception {

    private static final long serialVersionUID = 7905085686240307720L;

    /**
     * Create the new exception.
     * @param format message of the exception
     * @param arguments other parameters for the message
     */
    public InvalidJsonMsgException(final String format, final Object... arguments) {
        super(String.format(format, arguments));
    }
}
