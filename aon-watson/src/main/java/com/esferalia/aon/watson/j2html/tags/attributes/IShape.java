package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IShape<T extends Tag<T>> extends IInstance<T> {
    default T withShape(final String shape_) {
        return self().attr("shape", shape_);
    }

    default T withCondShape(final boolean enable, final String shape_) {
        if (enable) {
            self().attr("shape", shape_);
        }
        return self();
    }
}
