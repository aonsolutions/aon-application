package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ICite;
import com.esferalia.aon.watson.j2html.tags.attributes.IDatetime;

public final class InsTag extends ContainerTag<InsTag>
    implements ICite<InsTag>, IDatetime<InsTag> {
    public InsTag() {
        super("ins");
    }
}
