package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOntoggle<T extends Tag<T>> extends IInstance<T> {
    default T withOntoggle(final String ontoggle_) {
        return self().attr("ontoggle", ontoggle_);
    }

    default T withCondOntoggle(final boolean enable, final String ontoggle_) {
        if (enable) {
            self().attr("ontoggle", ontoggle_);
        }
        return self();
    }
}
