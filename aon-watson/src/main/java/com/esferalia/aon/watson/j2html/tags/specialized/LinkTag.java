package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IHref;
import com.esferalia.aon.watson.j2html.tags.attributes.IHreflang;
import com.esferalia.aon.watson.j2html.tags.attributes.IMedia;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.IRel;
import com.esferalia.aon.watson.j2html.tags.attributes.ISizes;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class LinkTag extends EmptyTag<LinkTag>
    implements IHref<LinkTag>, IHreflang<LinkTag>, IMedia<LinkTag>, IOnload<LinkTag>, IRel<LinkTag>, ISizes<LinkTag>, IType<LinkTag> {
    public LinkTag() {
        super("link");
    }
}
