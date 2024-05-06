package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IOncanplay<T extends Tag<T>> extends IInstance<T> {
    default T withOncanplay(final String oncanplay_) {
        return self().attr("oncanplay", oncanplay_);
    }

    default T withCondOncanplay(final boolean enable, final String oncanplay_) {
        if (enable) {
            self().attr("oncanplay", oncanplay_);
        }
        return self();
    }
}
