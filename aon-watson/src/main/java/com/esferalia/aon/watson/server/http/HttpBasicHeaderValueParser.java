package com.esferalia.aon.watson.server.http;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.esferalia.aon.watson.server.http.HTTP.HeaderElement;
import com.esferalia.aon.watson.server.http.HTTP.HeaderValueParser;
import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;

/**
 * Basic implementation for parsing header values into elements.
 * Instances of this class are stateless and thread-safe.
 * Derived classes are expected to maintain these properties.
 *
 * @since 4.0
 */
class HttpBasicHeaderValueParser implements HeaderValueParser {

    final static HttpBasicHeaderValueParser INSTANCE = new HttpBasicHeaderValueParser();

    final static char PARAM_DELIMITER                = ';';
    final static char ELEM_DELIMITER                 = ',';

    // IMPORTANT!
    // These private static variables must be treated as immutable and never exposed outside this class
    private static final BitSet TOKEN_DELIMS = HttpTokenParser.INIT_BITSET('=', PARAM_DELIMITER, ELEM_DELIMITER);
    private static final BitSet VALUE_DELIMS = HttpTokenParser.INIT_BITSET(PARAM_DELIMITER, ELEM_DELIMITER);

    private final HttpTokenParser tokenParser;

    HttpBasicHeaderValueParser() {
        this.tokenParser = HttpTokenParser.INSTANCE;
    }

    /**
     * Parses elements with the given parser.
     *
     * @param value     the header value to parse
     * @param parser    the parser to use, or {@code null} for default
     *
     * @return  array holding the header elements, never {@code null}
     * @throws HttpParseException in case of a parsing error
     */
    static
        HeaderElement[] parseElements(final String value,
                                      final HeaderValueParser parser) throws HttpParseException {
        HttpArgs.notNull(value, "Value");

        final HttpCharArrayBuffer buffer = new HttpCharArrayBuffer(value.length());
        buffer.append(value);
        final HttpParserCursor cursor = new HttpParserCursor(0, value.length());
        return (parser != null ? parser : HttpBasicHeaderValueParser.INSTANCE)
            .parseElements(buffer, cursor);
    }


    // non-javadoc, see interface HeaderValueParser
    @Override
    public HeaderElement[] parseElements(final HttpCharArrayBuffer buffer,
                                         final HttpParserCursor cursor) {
        HttpArgs.notNull(buffer, "Char array buffer");
        HttpArgs.notNull(cursor, "Parser cursor");
        final List<HeaderElement> elements = new ArrayList<HeaderElement>();
        while (!cursor.atEnd()) {
            final HeaderElement element = parseHeaderElement(buffer, cursor);
            if (!(element.getName().isEmpty() && element.getValue() == null)) {
                elements.add(element);
            }
        }
        return elements.toArray(new HeaderElement[elements.size()]);
    }


    /**
     * Parses an element with the given parser.
     *
     * @param value     the header element to parse
     * @param parser    the parser to use, or {@code null} for default
     *
     * @return  the parsed header element
     */
    static
        HeaderElement parseHeaderElement(final String value,
                                         final HeaderValueParser parser) throws HttpParseException {
        HttpArgs.notNull(value, "Value");

        final HttpCharArrayBuffer buffer = new HttpCharArrayBuffer(value.length());
        buffer.append(value);
        final HttpParserCursor cursor = new HttpParserCursor(0, value.length());
        return (parser != null ? parser : HttpBasicHeaderValueParser.INSTANCE)
                .parseHeaderElement(buffer, cursor);
    }


    // non-javadoc, see interface HeaderValueParser
    @Override
    public HeaderElement parseHeaderElement(final HttpCharArrayBuffer buffer,
                                            final HttpParserCursor cursor) {
        HttpArgs.notNull(buffer, "Char array buffer");
        HttpArgs.notNull(cursor, "Parser cursor");
        final NameValuePair nvp = parseNameValuePair(buffer, cursor);
        NameValuePair[] params = null;
        if (!cursor.atEnd()) {
            final char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch != ELEM_DELIMITER) {
                params = parseParameters(buffer, cursor);
            }
        }
        return createHeaderElement(nvp.getName(), nvp.getValue(), params);
    }


    /**
     * Creates a header element.
     * Called from {@link #parseHeaderElement}.
     *
     * @return  a header element representing the argument
     */
    protected HeaderElement createHeaderElement(
            final String name,
            final String value,
            final NameValuePair[] params) {
        return new HttpBasicHeaderElement(name, value, params);
    }


    /**
     * Parses parameters with the given parser.
     *
     * @param value     the parameter list to parse
     * @param parser    the parser to use, or {@code null} for default
     *
     * @return  array holding the parameters, never {@code null}
     */
    static
        NameValuePair[] parseParameters(final String value,
                                        final HeaderValueParser parser) throws HttpParseException {
        HttpArgs.notNull(value, "Value");

        final HttpCharArrayBuffer buffer = new HttpCharArrayBuffer(value.length());
        buffer.append(value);
        final HttpParserCursor cursor = new HttpParserCursor(0, value.length());
        return (parser != null ? parser : HttpBasicHeaderValueParser.INSTANCE)
                .parseParameters(buffer, cursor);
    }



    // non-javadoc, see interface HeaderValueParser
    @Override
    public NameValuePair[] parseParameters(final HttpCharArrayBuffer buffer,
                                           final HttpParserCursor cursor) {
        HttpArgs.notNull(buffer, "Char array buffer");
        HttpArgs.notNull(cursor, "Parser cursor");
        tokenParser.skipWhiteSpace(buffer, cursor);
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        while (!cursor.atEnd()) {
            final NameValuePair param = parseNameValuePair(buffer, cursor);
            params.add(param);
            final char ch = buffer.charAt(cursor.getPos() - 1);
            if (ch == ELEM_DELIMITER) {
                break;
            }
        }
        return params.toArray(new NameValuePair[params.size()]);
    }

    /**
     * Parses a name-value-pair with the given parser.
     *
     * @param value     the NVP to parse
     * @param parser    the parser to use, or {@code null} for default
     *
     * @return  the parsed name-value pair
     */
    static
       NameValuePair parseNameValuePair(final String value,
                                        final HeaderValueParser parser) throws HttpParseException {
        HttpArgs.notNull(value, "Value");

        final HttpCharArrayBuffer buffer = new HttpCharArrayBuffer(value.length());
        buffer.append(value);
        final HttpParserCursor cursor = new HttpParserCursor(0, value.length());
        return (parser != null ? parser : HttpBasicHeaderValueParser.INSTANCE)
                .parseNameValuePair(buffer, cursor);
    }


    // non-javadoc, see interface HeaderValueParser
    @Override
    public NameValuePair parseNameValuePair(final HttpCharArrayBuffer buffer,
                                            final HttpParserCursor cursor) {
        HttpArgs.notNull(buffer, "Char array buffer");
        HttpArgs.notNull(cursor, "Parser cursor");

        final String name = tokenParser.parseToken(buffer, cursor, TOKEN_DELIMS);
        if (cursor.atEnd()) {
            return new HttpBasicNameValuePair(name, null);
        }
        final int delim = buffer.charAt(cursor.getPos());
        cursor.updatePos(cursor.getPos() + 1);
        if (delim != '=') {
            return createNameValuePair(name, null);
        }
        final String value = tokenParser.parseValue(buffer, cursor, VALUE_DELIMS);
        if (!cursor.atEnd()) {
            cursor.updatePos(cursor.getPos() + 1);
        }
        return createNameValuePair(name, value);
    }

    /**
     * Creates a name-value pair.
     * Called from {@link #parseNameValuePair}.
     *
     * @param name      the name
     * @param value     the value, or {@code null}
     *
     * @return  a name-value pair representing the arguments
     */
    protected NameValuePair createNameValuePair(final String name, final String value) {
        return new HttpBasicNameValuePair(name, value);
    }

}

