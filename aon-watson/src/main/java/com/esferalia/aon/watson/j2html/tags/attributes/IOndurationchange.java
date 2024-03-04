package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOndurationchange<T extends Tag<T>> extends IInstance<T> {
    default T withOndurationchange(final String ondurationchange_) {
        return self().attr("ondurationchange", ondurationchange_);
    }

    default T withCondOndurationchange(final boolean enable, final String ondurationchange_) {
        if (enable) {
            self().attr("ondurationchange", ondurationchange_);
        }
        return self();
    }
}
