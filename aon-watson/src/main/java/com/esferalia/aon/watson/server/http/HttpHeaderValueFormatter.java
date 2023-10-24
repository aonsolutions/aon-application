package com.esferalia.aon.watson.server.http;

import com.esferalia.aon.watson.server.http.HTTP.HeaderElement;
import com.esferalia.aon.watson.server.http.HTTP.HeaderValueParser;
import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;

/**
 * Interface for formatting elements of a header value.
 * This is the complement to {@link HeaderValueParser}.
 * Instances of this interface are expected to be stateless and thread-safe.
 *
 * <p>
 * All formatting methods accept an optional buffer argument.
 * If a buffer is passed in, the formatted element will be appended
 * and the modified buffer is returned. If no buffer is passed in,
 * a new buffer will be created and filled with the formatted element.
 * In both cases, the caller is allowed to modify the returned buffer.
 * </p>
 *
 * @since 4.0
 */
interface HttpHeaderValueFormatter {

    /**
     * Formats an array of header elements.
     *
     * @param buffer    the buffer to append to, or
     *                  {@code null} to create a new buffer
     * @param elems     the header elements to format
     * @param quote     {@code true} to always format with quoted values,
     *                  {@code false} to use quotes only when necessary
     *
     * @return  a buffer with the formatted header elements.
     *          If the {@code buffer} argument was not {@code null},
     *          that buffer will be used and returned.
     */
    HttpCharArrayBuffer formatElements(HttpCharArrayBuffer buffer,
                                   HeaderElement[] elems,
                                   boolean quote);

    /**
     * Formats one header element.
     *
     * @param buffer    the buffer to append to, or
     *                  {@code null} to create a new buffer
     * @param elem      the header element to format
     * @param quote     {@code true} to always format with quoted values,
     *                  {@code false} to use quotes only when necessary
     *
     * @return  a buffer with the formatted header element.
     *          If the {@code buffer} argument was not {@code null},
     *          that buffer will be used and returned.
     */
    HttpCharArrayBuffer formatHeaderElement(HttpCharArrayBuffer buffer,
                                        HeaderElement elem,
                                        boolean quote);

    /**
     * Formats the parameters of a header element.
     * That's a list of name-value pairs, to be separated by semicolons.
     * This method will <i>not</i> generate a leading semicolon.
     *
     * @param buffer    the buffer to append to, or
     *                  {@code null} to create a new buffer
     * @param nvps      the parameters (name-value pairs) to format
     * @param quote     {@code true} to always format with quoted values,
     *                  {@code false} to use quotes only when necessary
     *
     * @return  a buffer with the formatted parameters.
     *          If the {@code buffer} argument was not {@code null},
     *          that buffer will be used and returned.
     */
    HttpCharArrayBuffer formatParameters(HttpCharArrayBuffer buffer,
                                     NameValuePair[] nvps,
                                     boolean quote);

    /**
     * Formats one name-value pair, where the value is optional.
     *
     * @param buffer    the buffer to append to, or
     *                  {@code null} to create a new buffer
     * @param nvp       the name-value pair to format
     * @param quote     {@code true} to always format with a quoted value,
     *                  {@code false} to use quotes only when necessary
     *
     * @return  a buffer with the formatted name-value pair.
     *          If the {@code buffer} argument was not {@code null},
     *          that buffer will be used and returned.
     */
    HttpCharArrayBuffer formatNameValuePair(HttpCharArrayBuffer buffer,
                                        NameValuePair nvp,
                                        boolean quote);

}

