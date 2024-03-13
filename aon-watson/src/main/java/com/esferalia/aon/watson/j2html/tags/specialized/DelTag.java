package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ICite;
import com.esferalia.aon.watson.j2html.tags.attributes.IDatetime;

public final class DelTag extends ContainerTag<DelTag>
    implements ICite<DelTag>, IDatetime<DelTag> {
    public DelTag() {
        super("del");
    }
}
