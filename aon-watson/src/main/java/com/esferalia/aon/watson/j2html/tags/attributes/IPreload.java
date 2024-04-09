package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IPreload<T extends Tag<T>> extends IInstance<T> {
    default T withPreload(final String preload_) {
        return self().attr("preload", preload_);
    }

    default T withCondPreload(final boolean enable, final String preload_) {
        if (enable) {
            self().attr("preload", preload_);
        }
        return self();
    }
}
