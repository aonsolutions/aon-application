package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;

public final class SlotTag extends ContainerTag<SlotTag>
    implements IName<SlotTag> {
    public SlotTag() {
        super("slot");
    }
}
