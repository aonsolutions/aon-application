package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IColspan;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeaders;
import com.esferalia.aon.watson.j2html.tags.attributes.IRowspan;

public final class TdTag extends ContainerTag<TdTag>
    implements IColspan<TdTag>, IHeaders<TdTag>, IRowspan<TdTag> {
    public TdTag() {
        super("td");
    }
}
