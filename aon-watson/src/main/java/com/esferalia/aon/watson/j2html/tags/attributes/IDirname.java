package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IDirname<T extends Tag<T>> extends IInstance<T> {
    default T withDirname(final String dirname_) {
        return self().attr("dirname", dirname_);
    }

    default T withCondDirname(final boolean enable, final String dirname_) {
        if (enable) {
            self().attr("dirname", dirname_);
        }
        return self();
    }
}
