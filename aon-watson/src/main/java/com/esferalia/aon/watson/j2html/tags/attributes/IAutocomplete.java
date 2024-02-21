package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IAutocomplete<T extends Tag<T>> extends IInstance<T> {
    default T isAutocomplete() {
        self().attr("autocomplete", "on");
        return self();
    }

    default T withCondAutocomplete(final boolean enable) {
        if (enable) {
            self().attr("autocomplete", "on");
        }
        return self();
    }
}
