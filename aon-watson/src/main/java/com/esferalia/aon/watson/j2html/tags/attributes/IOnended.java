package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOnended<T extends Tag<T>> extends IInstance<T> {
    default T withOnended(final String onended_) {
        return self().attr("onended", onended_);
    }

    default T withCondOnended(final boolean enable, final String onended_) {
        if (enable) {
            self().attr("onended", onended_);
        }
        return self();
    }
}
