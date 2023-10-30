package com.esferalia.aon.watson.server.http;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.esferalia.aon.watson.server.http.HTTP.Header;
import com.esferalia.aon.watson.server.http.HTTP.HeaderElement;
import com.esferalia.aon.watson.server.http.HTTP.HttpEntity;
import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;
import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Content type information consisting of a MIME type and an optional charset.
 * <p>
 * This class makes no attempts to verify validity of the MIME type.
 * The input parameters of the {@link #create(String, String)} method, however, may not
 * contain characters {@code <">, <;>, <,>} reserved by the HTTP specification.
 *
 * @since 4.2
 */

final class HttpContentType implements Serializable {

    private static final long serialVersionUID = -7768694718232371896L;

    // constants
    static final HttpContentType APPLICATION_ATOM_XML = create(
            "application/atom+xml", HTTP.ISO_8859_1);
    static final HttpContentType APPLICATION_FORM_URLENCODED = create(
            "application/x-www-form-urlencoded", HTTP.ISO_8859_1);
    static final HttpContentType APPLICATION_JSON = create(
            "application/json", HTTP.UTF_8);
    static final HttpContentType APPLICATION_OCTET_STREAM = create(
            "application/octet-stream", (Charset) null);
    static final HttpContentType APPLICATION_SOAP_XML = create(
            "application/soap+xml", HTTP.UTF_8);
    static final HttpContentType APPLICATION_SVG_XML = create(
            "application/svg+xml", HTTP.ISO_8859_1);
    static final HttpContentType APPLICATION_XHTML_XML = create(
            "application/xhtml+xml", HTTP.ISO_8859_1);
    static final HttpContentType APPLICATION_XML = create(
            "application/xml", HTTP.ISO_8859_1);
    static final HttpContentType IMAGE_BMP = create(
            "image/bmp");
    static final HttpContentType IMAGE_GIF= create(
            "image/gif");
    static final HttpContentType IMAGE_JPEG = create(
            "image/jpeg");
    static final HttpContentType IMAGE_PNG = create(
            "image/png");
    static final HttpContentType IMAGE_SVG= create(
            "image/svg+xml");
    static final HttpContentType IMAGE_TIFF = create(
            "image/tiff");
    static final HttpContentType IMAGE_WEBP = create(
            "image/webp");
    static final HttpContentType MULTIPART_FORM_DATA = create(
            "multipart/form-data", HTTP.ISO_8859_1);
    static final HttpContentType TEXT_HTML = create(
            "text/html", HTTP.ISO_8859_1);
    static final HttpContentType TEXT_PLAIN = create(
            "text/plain", HTTP.ISO_8859_1);
    static final HttpContentType TEXT_XML = create(
            "text/xml", HTTP.ISO_8859_1);
    static final HttpContentType WILDCARD = create(
            "*/*", (Charset) null);


    private static final Map<String, HttpContentType> CONTENT_TYPE_MAP;
    static {

        final HttpContentType[] contentTypes = {
            APPLICATION_ATOM_XML,
            APPLICATION_FORM_URLENCODED,
            APPLICATION_JSON,
            APPLICATION_SVG_XML,
            APPLICATION_XHTML_XML,
            APPLICATION_XML,
            IMAGE_BMP,
            IMAGE_GIF,
            IMAGE_JPEG,
            IMAGE_PNG,
            IMAGE_SVG,
            IMAGE_TIFF,
            IMAGE_WEBP,
            MULTIPART_FORM_DATA,
            TEXT_HTML,
            TEXT_PLAIN,
            TEXT_XML };
        final HashMap<String, HttpContentType> map = new HashMap<String, HttpContentType>();
        for (final HttpContentType contentType: contentTypes) {
            map.put(contentType.getMimeType(), contentType);
        }
        CONTENT_TYPE_MAP = Collections.unmodifiableMap(map);
    }

    // defaults
    static final HttpContentType DEFAULT_TEXT = TEXT_PLAIN;
    static final HttpContentType DEFAULT_BINARY = APPLICATION_OCTET_STREAM;

    private final String mimeType;
    private final Charset charset;
    private final NameValuePair[] params;

    HttpContentType(
            final String mimeType,
            final Charset charset) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = null;
    }

    HttpContentType(
            final String mimeType,
            final Charset charset,
            final NameValuePair[] params) {
        this.mimeType = mimeType;
        this.charset = charset;
        this.params = params;
    }

    String getMimeType() {
        return this.mimeType;
    }

    Charset getCharset() {
        return this.charset;
    }

    /**
     * @since 4.3
     */
    String getParameter(final String name) {
        HttpArgs.notEmpty(name, "Parameter name");
        if (this.params == null) {
            return null;
        }
        for (final NameValuePair param: this.params) {
            if (param.getName().equalsIgnoreCase(name)) {
                return param.getValue();
            }
        }
        return null;
    }

    /**
     * Generates textual representation of this content type which can be used as the value
     * of a {@code Content-Type} header.
     */
    @Override
    public String toString() {
        final HttpCharArrayBuffer buf = new HttpCharArrayBuffer(64);
        buf.append(this.mimeType);
        if (this.params != null) {
            buf.append("; ");
            HttpBasicHeaderValueFormatter.INSTANCE.formatParameters(buf, this.params, false);
        } else if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }

    private static boolean valid(final String s) {
        for (int i = 0; i < s.length(); i++) {
            final char ch = s.charAt(i);
            if (ch == '"' || ch == ',' || ch == ';') {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link HttpContentType}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *        characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset charset.
     * @return content type
     */
    static HttpContentType create(final String mimeType, final Charset charset) {
        final String normalizedMimeType = HttpArgs.notBlank(mimeType, "MIME type").toLowerCase(Locale.ROOT);
        HttpArgs.check(valid(normalizedMimeType), "MIME type may not contain reserved characters");
        return new HttpContentType(normalizedMimeType, charset);
    }

    /**
     * Creates a new instance of {@link HttpContentType} without a charset.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *        characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @return content type
     */
    static HttpContentType create(final String mimeType) {
        return create(mimeType, (Charset) null);
    }

    /**
     * Creates a new instance of {@link HttpContentType}.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *        characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param charset charset. It may not contain characters {@code <">, <;>, <,>} reserved by the HTTP
     *        specification. This parameter is optional.
     * @return content type
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     * this instance of the Java virtual machine
     */
    static HttpContentType create(
            final String mimeType, final String charset) throws UnsupportedCharsetException {
        return create(mimeType, !AonStringUtils.isBlank(charset) ? Charset.forName(charset) : null);
    }

    private static HttpContentType create(final HeaderElement helem, final boolean strict) {
        return create(helem.getName(), helem.getParameters(), strict);
    }

    private static HttpContentType create(final String mimeType, final NameValuePair[] params, final boolean strict) {
        Charset charset = null;
        for (final NameValuePair param: params) {
            if (param.getName().equalsIgnoreCase("charset")) {
                final String s = param.getValue();
                if (!AonStringUtils.isBlank(s)) {
                    try {
                        charset =  Charset.forName(s);
                    } catch (final UnsupportedCharsetException ex) {
                        if (strict) {
                            throw ex;
                        }
                    }
                }
                break;
            }
        }
        return new HttpContentType(mimeType, charset, params != null && params.length > 0 ? params : null);
    }

    /**
     * Creates a new instance of {@link HttpContentType} with the given parameters.
     *
     * @param mimeType MIME type. It may not be {@code null} or empty. It may not contain
     *        characters {@code <">, <;>, <,>} reserved by the HTTP specification.
     * @param params parameters.
     * @return content type
     *
     * @since 4.4
     */
    static HttpContentType create(
            final String mimeType, final NameValuePair... params) throws UnsupportedCharsetException {
        final String type = HttpArgs.notBlank(mimeType, "MIME type").toLowerCase(Locale.ROOT);
        HttpArgs.check(valid(type), "MIME type may not contain reserved characters");
        return create(mimeType, params, true);
    }

    /**
     * Parses textual representation of {@code Content-Type} value.
     *
     * @param s text
     * @return content type
     * @throws HttpParseException if the given text does not represent a valid
     * {@code Content-Type} value.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     * this instance of the Java virtual machine
     */
    static HttpContentType parse(
            final String s) throws HttpParseException, UnsupportedCharsetException {
        HttpArgs.notNull(s, "Content type");
        final HttpCharArrayBuffer buf = new HttpCharArrayBuffer(s.length());
        buf.append(s);
        final HttpParserCursor cursor = new HttpParserCursor(0, s.length());
        final HeaderElement[] elements = HttpBasicHeaderValueParser.INSTANCE.parseElements(buf, cursor);
        if (elements.length > 0) {
            return create(elements[0], true);
        }
        throw new HttpParseException("Invalid content type: " + s);
    }

    /**
     * Extracts {@code Content-Type} value from {@link HttpEntity} exactly as
     * specified by the {@code Content-Type} header of the entity. Returns {@code null}
     * if not specified.
     *
     * @param entity HTTP entity
     * @return content type
     * @throws HttpParseException if the given text does not represent a valid
     * {@code Content-Type} value.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     * this instance of the Java virtual machine
     */
    static HttpContentType get(
            final HttpEntity entity) throws HttpParseException, UnsupportedCharsetException {
        if (entity == null) {
            return null;
        }
        final Header header = entity.getContentType();
        if (header != null) {
            final HeaderElement[] elements = header.getElements();
            if (elements.length > 0) {
                return create(elements[0], true);
            }
        }
        return null;
    }

    /**
     * Extracts {@code Content-Type} value from {@link HttpEntity}. Returns {@code null}
     * if not specified or incorrect (could not be parsed)..
     *
     * @param entity HTTP entity
     * @return content type
     *
     * @since 4.4
     *
     */
    static HttpContentType getLenient(final HttpEntity entity) {
        if (entity == null) {
            return null;
        }
        final Header header = entity.getContentType();
        if (header != null) {
            try {
                final HeaderElement[] elements = header.getElements();
                if (elements.length > 0) {
                    return create(elements[0], false);
                }
            } catch (final HttpParseException ex) {
                return null;
            }
        }
        return null;
    }

    /**
     * Extracts {@code Content-Type} value from {@link HttpEntity} or returns the default value
     * {@link #DEFAULT_TEXT} if not explicitly specified.
     *
     * @param entity HTTP entity
     * @return content type
     * @throws HttpParseException if the given text does not represent a valid
     * {@code Content-Type} value.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     * this instance of the Java virtual machine
     */
    static HttpContentType getOrDefault(
            final HttpEntity entity) throws HttpParseException, UnsupportedCharsetException {
        final HttpContentType contentType = get(entity);
        return contentType != null ? contentType : DEFAULT_TEXT;
    }

    /**
     * Extracts {@code Content-Type} value from {@link HttpEntity} or returns the default value
     * {@link #DEFAULT_TEXT} if not explicitly specified or incorrect (could not be parsed).
     *
     * @param entity HTTP entity
     * @return content type
     *
     * @since 4.4
     */
    static HttpContentType getLenientOrDefault(
            final HttpEntity entity) throws HttpParseException, UnsupportedCharsetException {
        final HttpContentType contentType = get(entity);
        return contentType != null ? contentType : DEFAULT_TEXT;
    }


    /**
     * Returns {@code Content-Type} for the given MIME type.
     *
     * @param mimeType MIME type
     * @return content type or {@code null} if not known.
     *
     * @since 4.5
     */
    static HttpContentType getByMimeType(final String mimeType) {
        if (mimeType == null) {
            return null;
        }
        return CONTENT_TYPE_MAP.get(mimeType);
    }

    /**
     * Creates a new instance with this MIME type and the given Charset.
     *
     * @param charset charset
     * @return a new instance with this MIME type and the given Charset.
     * @since 4.3
     */
    HttpContentType withCharset(final Charset charset) {
        return create(this.getMimeType(), charset);
    }

    /**
     * Creates a new instance with this MIME type and the given Charset name.
     *
     * @param charset name
     * @return a new instance with this MIME type and the given Charset name.
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     * this instance of the Java virtual machine
     * @since 4.3
     */
    HttpContentType withCharset(final String charset) {
        return create(this.getMimeType(), charset);
    }

    /**
     * Creates a new instance with this MIME type and the given parameters.
     *
     * @param params
     * @return a new instance with this MIME type and the given parameters.
     * @since 4.4
     */
    HttpContentType withParameters(
            final NameValuePair... params) throws UnsupportedCharsetException {
        if (params.length == 0) {
            return this;
        }
        final Map<String, String> paramMap = new LinkedHashMap<String, String>();
        if (this.params != null) {
            for (final NameValuePair param: this.params) {
                paramMap.put(param.getName(), param.getValue());
            }
        }
        for (final NameValuePair param: params) {
            paramMap.put(param.getName(), param.getValue());
        }
        final List<NameValuePair> newParams = new ArrayList<NameValuePair>(paramMap.size() + 1);
        if (this.charset != null && !paramMap.containsKey("charset")) {
            newParams.add(new HttpBasicNameValuePair("charset", this.charset.name()));
        }
        for (final Map.Entry<String, String> entry: paramMap.entrySet()) {
            newParams.add(new HttpBasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return create(this.getMimeType(), newParams.toArray(new NameValuePair[newParams.size()]), true);
    }

}
