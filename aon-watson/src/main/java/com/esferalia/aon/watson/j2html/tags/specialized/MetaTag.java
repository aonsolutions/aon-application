package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.ICharset;
import com.esferalia.aon.watson.j2html.tags.attributes.IContent;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;

public final class MetaTag extends EmptyTag<MetaTag>
    implements ICharset<MetaTag>, IContent<MetaTag>, IName<MetaTag> {
    public MetaTag() {
        super("meta");
    }
}
