package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOnstorage<T extends Tag<T>> extends IInstance<T> {
    default T withOnstorage(final String onstorage_) {
        return self().attr("onstorage", onstorage_);
    }

    default T withCondOnstorage(final boolean enable, final String onstorage_) {
        if (enable) {
            self().attr("onstorage", onstorage_);
        }
        return self();
    }
}
