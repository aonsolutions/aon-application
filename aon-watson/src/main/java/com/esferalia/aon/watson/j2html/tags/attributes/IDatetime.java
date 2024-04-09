package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IDatetime<T extends Tag<T>> extends IInstance<T> {
    default T withDatetime(final String datetime_) {
        return self().attr("datetime", datetime_);
    }

    default T withCondDatetime(final boolean enable, final String datetime_) {
        if (enable) {
            self().attr("datetime", datetime_);
        }
        return self();
    }
}
