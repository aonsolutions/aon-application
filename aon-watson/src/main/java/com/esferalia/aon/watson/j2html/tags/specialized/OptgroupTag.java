package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.ILabel;

public final class OptgroupTag extends ContainerTag<OptgroupTag>
    implements IDisabled<OptgroupTag>, ILabel<OptgroupTag> {
    public OptgroupTag() {
        super("optgroup");
    }
}
