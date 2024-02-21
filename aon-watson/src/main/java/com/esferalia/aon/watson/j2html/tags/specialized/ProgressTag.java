package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IMax;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class ProgressTag extends ContainerTag<ProgressTag>
    implements IMax<ProgressTag>, IValue<ProgressTag> {
    public ProgressTag() {
        super("progress");
    }
}
