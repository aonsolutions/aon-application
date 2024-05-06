package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOnpageshow<T extends Tag<T>> extends IInstance<T> {
    default T withOnpageshow(final String onpageshow_) {
        return self().attr("onpageshow", onpageshow_);
    }

    default T withCondOnpageshow(final boolean enable, final String onpageshow_) {
        if (enable) {
            self().attr("onpageshow", onpageshow_);
        }
        return self();
    }
}
