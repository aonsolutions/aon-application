package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface INovalidate<T extends Tag<T>> extends IInstance<T> {
    default T isNovalidate() {
        self().attr("novalidate");
        return self();
    }

    default T withCondNovalidate(final boolean enable) {
        if (enable) {
            self().attr("novalidate");
        }
        return self();
    }
}
