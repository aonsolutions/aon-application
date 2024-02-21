package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IReversed;
import com.esferalia.aon.watson.j2html.tags.attributes.IStart;

public final class OlTag extends ContainerTag<OlTag>
    implements IReversed<OlTag>, IStart<OlTag> {
    public OlTag() {
        super("ol");
    }
}
