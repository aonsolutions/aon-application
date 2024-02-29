package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IForm<T extends Tag<T>> extends IInstance<T> {
    default T withForm(final String form_) {
        return self().attr("form", form_);
    }

    default T withCondForm(final boolean enable, final String form_) {
        if (enable) {
            self().attr("form", form_);
        }
        return self();
    }
}
