package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ISpan;

public final class ColgroupTag extends ContainerTag<ColgroupTag>
    implements ISpan<ColgroupTag> {
    public ColgroupTag() {
        super("colgroup");
    }
}
