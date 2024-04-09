package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class CanvasTag extends ContainerTag<CanvasTag>
    implements IHeight<CanvasTag>, IWidth<CanvasTag> {
    public CanvasTag() {
        super("canvas");
    }
}
