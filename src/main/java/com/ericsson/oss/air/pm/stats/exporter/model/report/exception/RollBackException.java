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

package com.ericsson.oss.air.pm.stats.exporter.model.report.exception;

/**
 * Class to gracefully handle any exception during transactional operations.
 */
public class RollBackException extends RuntimeException {
    private static final long serialVersionUID = 3390503203974298264L;

    /**
     * Create new exception.
     * @param message of the exception
     */
    public RollBackException(final String message) {
        super(message);
    }

    /**
     * Create new exception.
     * @param message of the exception
     * @param cause the throwable object
     */
    public RollBackException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
