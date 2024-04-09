package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class ParamTag extends EmptyTag<ParamTag>
    implements IName<ParamTag>, IValue<ParamTag> {
    public ParamTag() {
        super("param");
    }
}
