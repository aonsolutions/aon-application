package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ISpan;

public final class ColTag extends EmptyTag<ColTag>
    implements ISpan<ColTag> {
    public ColTag() {
        super("col");
    }
}
