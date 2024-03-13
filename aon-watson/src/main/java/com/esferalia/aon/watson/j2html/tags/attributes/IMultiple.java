package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IMultiple<T extends Tag<T>> extends IInstance<T> {
    default T isMultiple() {
        self().attr("multiple");
        return self();
    }

    default T withCondMultiple(final boolean enable) {
        if (enable) {
            self().attr("multiple");
        }
        return self();
    }
}
