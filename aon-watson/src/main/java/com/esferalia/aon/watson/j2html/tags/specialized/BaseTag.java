package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IHref;
import com.esferalia.aon.watson.j2html.tags.attributes.ITarget;

public final class BaseTag extends EmptyTag<BaseTag>
    implements IHref<BaseTag>, ITarget<BaseTag> {
    public BaseTag() {
        super("base");
    }
}
