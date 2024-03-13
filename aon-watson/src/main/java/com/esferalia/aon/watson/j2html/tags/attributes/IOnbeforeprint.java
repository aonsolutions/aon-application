package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOnbeforeprint<T extends Tag<T>> extends IInstance<T> {
    default T withOnbeforeprint(final String onbeforeprint_) {
        return self().attr("onbeforeprint", onbeforeprint_);
    }

    default T withCondOnbeforeprint(final boolean enable, final String onbeforeprint_) {
        if (enable) {
            self().attr("onbeforeprint", onbeforeprint_);
        }
        return self();
    }
}
