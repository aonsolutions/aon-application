package com.esferalia.aon.watson.j2html.tags.attributes;

import com.esferalia.aon.watson.j2html.tags.IInstance;
import com.esferalia.aon.watson.j2html.tags.Tag;

public interface IDownload<T extends Tag<T>> extends IInstance<T> {
    default T isDownload() {
        self().attr("download");
        return self();
    }

    default T withCondDownload(final boolean enable) {
        if (enable) {
            self().attr("download");
        }
        return self();
    }
}
