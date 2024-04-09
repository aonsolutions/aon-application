package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOnresize<T extends Tag<T>> extends IInstance<T> {
    default T withOnresize(final String onresize_) {
        return self().attr("onresize", onresize_);
    }

    default T withCondOnresize(final boolean enable, final String onresize_) {
        if (enable) {
            self().attr("onresize", onresize_);
        }
        return self();
    }
}
