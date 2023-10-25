package com.esferalia.aon.watson.server.http;

import com.esferalia.aon.watson.server.http.HTTP.HeaderElement;
import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;

/**
 * Basic implementation of {@link HeaderElement}
 *
 * @since 4.0
 */
class HttpBasicHeaderElement implements HeaderElement, Cloneable {

    private final String name;
    private final String value;
    private final NameValuePair[] parameters;

    /**
     * Constructor with name, value and parameters.
     *
     * @param name header element name
     * @param value header element value. May be {@code null}
     * @param parameters header element parameters. May be {@code null}.
     *   Parameters are copied by reference, not by value
     */
     HttpBasicHeaderElement(
            final String name,
            final String value,
            final NameValuePair[] parameters) {
        super();
        this.name = HttpArgs.notNull(name, "Name");
        this.value = value;
        if (parameters != null) {
            this.parameters = parameters;
        } else {
            this.parameters = new NameValuePair[] {};
        }
    }

    /**
     * Constructor with name and value.
     *
     * @param name header element name
     * @param value header element value. May be {@code null}
     */
    HttpBasicHeaderElement(final String name, final String value) {
       this(name, value, null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public NameValuePair[] getParameters() {
        return this.parameters.clone();
    }

    @Override
    public int getParameterCount() {
        return this.parameters.length;
    }

    @Override
    public NameValuePair getParameter(final int index) {
        // ArrayIndexOutOfBoundsException is appropriate
        return this.parameters[index];
    }

    @Override
    public NameValuePair getParameterByName(final String name) {
        HttpArgs.notNull(name, "Name");
        NameValuePair found = null;
        for (final NameValuePair current : this.parameters) {
            if (current.getName().equalsIgnoreCase(name)) {
                found = current;
                break;
            }
        }
        return found;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof HeaderElement) {
            final HttpBasicHeaderElement that = (HttpBasicHeaderElement) object;
            return this.name.equals(that.name)
                && HttpLangUtils.equals(this.value, that.value)
                && HttpLangUtils.equals(this.parameters, that.parameters);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = HttpLangUtils.HASH_SEED;
        hash = HttpLangUtils.hashCode(hash, this.name);
        hash = HttpLangUtils.hashCode(hash, this.value);
        for (final NameValuePair parameter : this.parameters) {
            hash = HttpLangUtils.hashCode(hash, parameter);
        }
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            buffer.append(this.value);
        }
        for (final NameValuePair parameter : this.parameters) {
            buffer.append("; ");
            buffer.append(parameter);
        }
        return buffer.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // parameters array is considered immutable
        // no need to make a copy of it
        return super.clone();
    }

}

