package com.esferalia.aon.watson.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Constants and static helpers related to the HTTP protocol.
 *
 * @since 4.0
 */
final class HTTP {

    static final int CR = 13; // <US-ASCII CR, carriage return (13)>
    static final int LF = 10; // <US-ASCII LF, linefeed (10)>
    static final int SP = 32; // <US-ASCII SP, space (32)>
    static final int HT = 9;  // <US-ASCII HT, horizontal-tab (9)>

    /** HTTP header definitions */
    static final String TRANSFER_ENCODING = "Transfer-Encoding";
    static final String CONTENT_LEN  = "Content-Length";
    static final String CONTENT_TYPE = "Content-Type";
    static final String CONTENT_ENCODING = "Content-Encoding";
    static final String EXPECT_DIRECTIVE = "Expect";
    static final String CONN_DIRECTIVE = "Connection";
    static final String TARGET_HOST = "Host";
    static final String USER_AGENT = "User-Agent";
    static final String DATE_HEADER = "Date";
    static final String SERVER_HEADER = "Server";

    /** HTTP expectations */
    static final String EXPECT_CONTINUE = "100-continue";

    /** HTTP connection control */
    static final String CONN_CLOSE = "Close";
    static final String CONN_KEEP_ALIVE = "Keep-Alive";

    /** Transfer encoding definitions */
    static final String CHUNK_CODING = "chunked";
    static final String IDENTITY_CODING = "identity";

    static final Charset DEF_CONTENT_CHARSET = HTTP.ISO_8859_1;
    static final Charset DEF_PROTOCOL_CHARSET = HTTP.ASCII;
    static final Charset UTF_8 = Charset.forName("UTF-8");
    static final Charset ASCII = Charset.forName("US-ASCII");
    static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    static boolean isWhitespace(final char ch) {
        return ch == SP || ch == HT || ch == CR || ch == LF;
    }

    private HTTP() {
    }

    interface NameValuePair {
        String getName();
        String getValue();
    }
    
    interface HeaderElement {
        String getName();
        String getValue();
        NameValuePair[] getParameters();
        NameValuePair getParameterByName(String name);
        int getParameterCount();
        NameValuePair getParameter(int index);
    }

    interface Header extends NameValuePair {
        HeaderElement[] getElements() throws HttpParseException;
    }
    
    interface HeaderValueParser {
        HeaderElement[] parseElements(HttpCharArrayBuffer buffer,HttpParserCursor cursor) throws HttpParseException;
        HeaderElement parseHeaderElement(HttpCharArrayBuffer buffer,HttpParserCursor cursor) throws HttpParseException;
        NameValuePair[] parseParameters(HttpCharArrayBuffer buffer,HttpParserCursor cursor) throws HttpParseException;
        NameValuePair parseNameValuePair(HttpCharArrayBuffer buffer,HttpParserCursor cursor) throws HttpParseException;
    }
    
    interface HttpEntity {
        boolean isRepeatable();
        boolean isChunked();
        long getContentLength();
        Header getContentType();
        Header getContentEncoding();
        InputStream getContent() throws IOException, UnsupportedOperationException;
        void writeTo(OutputStream outStream) throws IOException;
        boolean isStreaming(); // don't expect an exception here
    }
    
    
}
