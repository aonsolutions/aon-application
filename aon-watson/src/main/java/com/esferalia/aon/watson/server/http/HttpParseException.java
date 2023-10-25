package com.esferalia.aon.watson.server.http;

import java.net.ProtocolException;

/**
 * Signals a parse error.
 * Parse errors when receiving a message will typically trigger
 * {@link ProtocolException}. Parse errors that do not occur during
 * protocol execution may be handled differently.
 * This is an unchecked exception, since there are cases where
 * the data to be parsed has been generated and is therefore
 * known to be parseable.
 *
 * @since 4.0
 */
class HttpParseException extends RuntimeException {

    private static final long serialVersionUID = -7288819855864183578L;

    /**
     * Creates a {@link HttpParseException} without details.
     */
    HttpParseException() {
        super();
    }

    /**
     * Creates a {@link HttpParseException} with a detail message.
     *
     * @param message the exception detail message, or {@code null}
     */
    HttpParseException(final String message) {
        super(message);
    }

}
