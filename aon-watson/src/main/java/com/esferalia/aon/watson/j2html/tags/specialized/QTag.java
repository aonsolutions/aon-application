package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ICite;

public final class QTag extends ContainerTag<QTag>
    implements ICite<QTag> {
    public QTag() {
        super("q");
    }
}
