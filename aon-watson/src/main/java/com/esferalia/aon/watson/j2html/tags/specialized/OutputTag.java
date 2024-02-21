package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IFor;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;

public final class OutputTag extends ContainerTag<OutputTag>
    implements IFor<OutputTag>, IForm<OutputTag>, IName<OutputTag> {
    public OutputTag() {
        super("output");
    }
}
