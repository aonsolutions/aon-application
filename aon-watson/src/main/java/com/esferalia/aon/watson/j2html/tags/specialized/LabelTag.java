package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IFor;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;

public final class LabelTag extends ContainerTag<LabelTag>
    implements IFor<LabelTag>, IForm<LabelTag> {
    public LabelTag() {
        super("label");
    }
}
