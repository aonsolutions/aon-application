package com.esferalia.aon.watson.server.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;
import com.esferalia.aon.watson.util.AonStringUtils;


/**
 * Builder for {@link URI} instances.
 *
 * @since 4.2
 */
public class AonURIBuilder {

    private String scheme;
    private String encodedSchemeSpecificPart;
    private String encodedAuthority;
    private String userInfo;
    private String encodedUserInfo;
    private String host;
    private int port;
    private String encodedPath;
    private List<String> pathSegments;
    private String encodedQuery;
    private List<NameValuePair> queryParams;
    private String query;
    private Charset charset;
    private String fragment;
    private String encodedFragment;

    /**
     * Constructs an empty instance.
     */
    public AonURIBuilder() {
        super();
        this.port = -1;
    }

    /**
     * Construct an instance from the string which must be a valid URI.
     *
     * @param string a valid URI in string form
     * @throws URISyntaxException if the input is not a valid URI
     */
    public AonURIBuilder(final String string) throws URISyntaxException {
        this(new URI(string), null);
    }

    /**
     * Construct an instance from the provided URI.
     * @param uri
     */
    public AonURIBuilder(final URI uri) {
        this(uri, null);
    }

    /**
     * Construct an instance from the string which must be a valid URI.
     *
     * @param string a valid URI in string form
     * @throws URISyntaxException if the input is not a valid URI
     */
    public AonURIBuilder(final String string, final Charset charset) throws URISyntaxException {
        this(new URI(string), charset);
    }

    /**
     * Construct an instance from the provided URI.
     * @param uri
     */
    public AonURIBuilder(final URI uri, final Charset charset) {
        super();
        setCharset(charset);
        digestURI(uri);
    }

    /**
     * @since 4.4
     */
    public AonURIBuilder setCharset(final Charset charset) {
        this.charset = charset;
        return this;
    }

    /**
     * @since 4.4
     */
    public Charset getCharset() {
        return charset;
    }

    private List <NameValuePair> parseQuery(final String query, final Charset charset) {
        if (query != null && !query.isEmpty()) {
            return HttpURLEncodedUtils.parse(query, charset);
        }
        return null;
    }

    private List <String> parsePath(final String path, final Charset charset) {
        if (path != null && !path.isEmpty()) {
            return HttpURLEncodedUtils.parsePathSegments(path, charset);
        }
        return null;
    }

    /**
     * Builds a {@link URI} instance.
     */
    public URI build() throws URISyntaxException {
        return new URI(buildString());
    }

    private String buildString() {
        final StringBuilder sb = new StringBuilder();
        if (this.scheme != null) {
            sb.append(this.scheme).append(':');
        }
        if (this.encodedSchemeSpecificPart != null) {
            sb.append(this.encodedSchemeSpecificPart);
        } else {
            if (this.encodedAuthority != null) {
                sb.append("//").append(this.encodedAuthority);
            } else if (this.host != null) {
                sb.append("//");
                if (this.encodedUserInfo != null) {
                    sb.append(this.encodedUserInfo).append("@");
                } else if (this.userInfo != null) {
                    sb.append(encodeUserInfo(this.userInfo)).append("@");
                }
                if (AonInetAddressUtils.isIPv6Address(this.host)) {
                    sb.append("[").append(this.host).append("]");
                } else {
                    sb.append(this.host);
                }
                if (this.port >= 0) {
                    sb.append(":").append(this.port);
                }
            }
            if (this.encodedPath != null) {
                sb.append(normalizePath(this.encodedPath, sb.length() == 0));
            } else if (this.pathSegments != null) {
                sb.append(encodePath(this.pathSegments));
            }
            if (this.encodedQuery != null) {
                sb.append("?").append(this.encodedQuery);
            } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
                sb.append("?").append(encodeUrlForm(this.queryParams));
            } else if (this.query != null) {
                sb.append("?").append(encodeUric(this.query));
            }
        }
        if (this.encodedFragment != null) {
            sb.append("#").append(this.encodedFragment);
        } else if (this.fragment != null) {
            sb.append("#").append(encodeUric(this.fragment));
        }
        return sb.toString();
    }

    private static String normalizePath(final String path, final boolean relative) {
        String s = path;
        if (AonStringUtils.isBlank(s)) {
            return "";
        }
        if (!relative && !s.startsWith("/")) {
            s = "/" + s;
        }
        return s;
    }

    private void digestURI(final URI uri) {
        this.scheme = uri.getScheme();
        this.encodedSchemeSpecificPart = uri.getRawSchemeSpecificPart();
        this.encodedAuthority = uri.getRawAuthority();
        this.host = uri.getHost();
        this.port = uri.getPort();
        this.encodedUserInfo = uri.getRawUserInfo();
        this.userInfo = uri.getUserInfo();
        this.encodedPath = uri.getRawPath();
        this.pathSegments = parsePath(uri.getRawPath(), this.charset != null ? this.charset : HTTP.UTF_8);
        this.encodedQuery = uri.getRawQuery();
        this.queryParams = parseQuery(uri.getRawQuery(), this.charset != null ? this.charset : HTTP.UTF_8);
        this.encodedFragment = uri.getRawFragment();
        this.fragment = uri.getFragment();
    }

    private String encodeUserInfo(final String userInfo) {
        return HttpURLEncodedUtils.encUserInfo(userInfo, this.charset != null ? this.charset : HTTP.UTF_8);
    }

    private String encodePath(final List<String> pathSegments) {
        return HttpURLEncodedUtils.formatSegments(pathSegments, this.charset != null ? this.charset : HTTP.UTF_8);
    }

    private String encodeUrlForm(final List<NameValuePair> params) {
        return HttpURLEncodedUtils.format(params, this.charset != null ? this.charset : HTTP.UTF_8);
    }

    private String encodeUric(final String fragment) {
        return HttpURLEncodedUtils.encUric(fragment, this.charset != null ? this.charset : HTTP.UTF_8);
    }

    /**
     * Sets URI scheme.
     */
    public AonURIBuilder setScheme(final String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Sets URI user info. The value is expected to be unescaped and may contain non ASCII
     * characters.
     */
    public AonURIBuilder setUserInfo(final String userInfo) {
        this.userInfo = userInfo;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        this.encodedUserInfo = null;
        return this;
    }

    /**
     * Sets URI user info as a combination of username and password. These values are expected to
     * be unescaped and may contain non ASCII characters.
     */
    public AonURIBuilder setUserInfo(final String username, final String password) {
        return setUserInfo(username + ':' + password);
    }

    /**
     * Sets URI host.
     */
    public AonURIBuilder setHost(final String host) {
        this.host = host;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    /**
     * Sets URI port.
     */
    public AonURIBuilder setPort(final int port) {
        this.port = port < 0 ? -1 : port;
        this.encodedSchemeSpecificPart = null;
        this.encodedAuthority = null;
        return this;
    }

    /**
     * Sets URI path. The value is expected to be unescaped and may contain non ASCII characters.
     *
     * @return this.
     */
    public AonURIBuilder setPath(final String path) {
        return setPathSegments(path != null ? HttpURLEncodedUtils.splitPathSegments(path) : null);
    }

    /**
     * Sets URI path. The value is expected to be unescaped and may contain non ASCII characters.
     *
     * @return this.
     *
     * @since 4.5.8
     */
    public AonURIBuilder setPathSegments(final String... pathSegments) {
        this.pathSegments = pathSegments.length > 0 ? Arrays.asList(pathSegments) : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        return this;
    }

    /**
     * Sets URI path. The value is expected to be unescaped and may contain non ASCII characters.
     *
     * @return this.
     *
     * @since 4.5.8
     */
    public AonURIBuilder setPathSegments(final List<String> pathSegments) {
        this.pathSegments = pathSegments != null && pathSegments.size() > 0 ? new ArrayList<String>(pathSegments) : null;
        this.encodedSchemeSpecificPart = null;
        this.encodedPath = null;
        return this;
    }

    /**
     * Removes URI query.
     */
    public AonURIBuilder removeQuery() {
        this.queryParams = null;
        this.query = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    /**
     * Sets URI query parameters. The parameter name / values are expected to be unescaped
     * and may contain non ASCII characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove custom query if present.
     * </p>
     *
     * @since 4.3
     */
    public AonURIBuilder setParameters(final List <NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    /**
     * Adds URI query parameters. The parameter name / values are expected to be unescaped
     * and may contain non ASCII characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove custom query if present.
     * </p>
     *
     * @since 4.3
     */
    public AonURIBuilder addParameters(final List <NameValuePair> nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.addAll(nvps);
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    /**
     * Sets URI query parameters. The parameter name / values are expected to be unescaped
     * and may contain non ASCII characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove custom query if present.
     * </p>
     *
     * @since 4.3
     */
    public AonURIBuilder setParameters(final NameValuePair... nvps) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        } else {
            this.queryParams.clear();
        }
        for (final NameValuePair nvp: nvps) {
            this.queryParams.add(nvp);
        }
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    /**
     * Adds parameter to URI query. The parameter name and value are expected to be unescaped
     * and may contain non ASCII characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove custom query if present.
     * </p>
     */
    public AonURIBuilder addParameter(final String param, final String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        this.queryParams.add(new HttpBasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    /**
     * Sets parameter of URI query overriding existing value if set. The parameter name and value
     * are expected to be unescaped and may contain non ASCII characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove custom query if present.
     * </p>
     */
    public AonURIBuilder setParameter(final String param, final String value) {
        if (this.queryParams == null) {
            this.queryParams = new ArrayList<NameValuePair>();
        }
        if (!this.queryParams.isEmpty()) {
            for (final Iterator<NameValuePair> it = this.queryParams.iterator(); it.hasNext(); ) {
                final NameValuePair nvp = it.next();
                if (nvp.getName().equals(param)) {
                    it.remove();
                }
            }
        }
        this.queryParams.add(new HttpBasicNameValuePair(param, value));
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.query = null;
        return this;
    }

    /**
     * Clears URI query parameters.
     *
     * @since 4.3
     */
    public AonURIBuilder clearParameters() {
        this.queryParams = null;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        return this;
    }

    /**
     * Sets custom URI query. The value is expected to be unescaped and may contain non ASCII
     * characters.
     * <p>
     * Please note query parameters and custom query component are mutually exclusive. This method
     * will remove query parameters if present.
     * </p>
     *
     * @since 4.3
     */
    public AonURIBuilder setCustomQuery(final String query) {
        this.query = query;
        this.encodedQuery = null;
        this.encodedSchemeSpecificPart = null;
        this.queryParams = null;
        return this;
    }

    /**
     * Sets URI fragment. The value is expected to be unescaped and may contain non ASCII
     * characters.
     */
    public AonURIBuilder setFragment(final String fragment) {
        this.fragment = fragment;
        this.encodedFragment = null;
        return this;
    }

    /**
     * @since 4.3
     */
    public boolean isAbsolute() {
        return this.scheme != null;
    }

    /**
     * @since 4.3
     */
    public boolean isOpaque() {
        return this.pathSegments == null && this.encodedPath == null;
    }

    public String getScheme() {
        return this.scheme;
    }

    public String getUserInfo() {
        return this.userInfo;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    /**
     * @since 4.5.8
     */
    public boolean isPathEmpty() {
        return (this.pathSegments == null || this.pathSegments.isEmpty()) &&
               (this.encodedPath == null || this.encodedPath.isEmpty());
    }

    /**
     * @since 4.5.8
     */
    public List<String> getPathSegments() {
        return this.pathSegments != null ? new ArrayList<String>(this.pathSegments) : Collections.<String>emptyList();
    }

    public String getPath() {
        if (this.pathSegments == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        for (final String segment : this.pathSegments) {
            result.append('/').append(segment);
        }
        return result.toString();
    }

    /**
     * @since 4.5.8
     */
    public boolean isQueryEmpty() {
        return (this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null;
    }

    public List<NameValuePair> getQueryParams() {
        return this.queryParams != null ? new ArrayList<NameValuePair>(this.queryParams) : Collections.<NameValuePair>emptyList();
    }

    public String getFragment() {
        return this.fragment;
    }

    @Override
    public String toString() {
        return buildString();
    }

}
