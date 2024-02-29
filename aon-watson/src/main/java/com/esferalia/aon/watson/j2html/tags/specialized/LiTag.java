package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class LiTag extends ContainerTag<LiTag>
    implements IValue<LiTag> {
    public LiTag() {
        super("li");
    }
}
