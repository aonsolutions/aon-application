package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IColspan;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeaders;
import com.esferalia.aon.watson.j2html.tags.attributes.IRowspan;
import com.esferalia.aon.watson.j2html.tags.attributes.IScope;

public final class ThTag extends ContainerTag<ThTag>
    implements IColspan<ThTag>, IHeaders<ThTag>, IRowspan<ThTag>, IScope<ThTag> {
    public ThTag() {
        super("th");
    }
}
