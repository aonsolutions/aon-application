package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ICite;

public final class BlockquoteTag extends ContainerTag<BlockquoteTag>
    implements ICite<BlockquoteTag> {
    public BlockquoteTag() {
        super("blockquote");
    }
}
