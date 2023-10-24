package com.esferalia.aon.watson.server.http;

import java.io.Serializable;

import com.esferalia.aon.watson.server.http.HTTP.NameValuePair;


/**
 * Basic implementation of {@link NameValuePair}.
 *
 * @since 4.0
 */
class HttpBasicNameValuePair implements NameValuePair, Cloneable, Serializable {

    private static final long serialVersionUID = -6437800749411518984L;

    private final String name;
    private final String value;

    /**
     * Default Constructor taking a name and a value. The value may be null.
     *
     * @param name The name.
     * @param value The value.
     */
    HttpBasicNameValuePair(final String name, final String value) {
        super();
        this.name = HttpArgs.notNull(name, "Name");
        this.value = value;
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
    public String toString() {
        // don't call complex default formatting for a simple toString

        if (this.value == null) {
            return name;
        }
        final int len = this.name.length() + 1 + this.value.length();
        final StringBuilder buffer = new StringBuilder(len);
        buffer.append(this.name);
        buffer.append("=");
        buffer.append(this.value);
        return buffer.toString();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            final HttpBasicNameValuePair that = (HttpBasicNameValuePair) object;
            return this.name.equals(that.name)
                  && HttpLangUtils.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = HttpLangUtils.HASH_SEED;
        hash = HttpLangUtils.hashCode(hash, this.name);
        hash = HttpLangUtils.hashCode(hash, this.value);
        return hash;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
