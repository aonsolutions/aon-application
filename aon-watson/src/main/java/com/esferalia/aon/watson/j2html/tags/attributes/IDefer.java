package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IDefer<T extends Tag<T>> extends IInstance<T> {
    default T isDefer() {
        self().attr("defer");
        return self();
    }

    default T withCondDefer(final boolean enable) {
        if (enable) {
            self().attr("defer");
        }
        return self();
    }
}
