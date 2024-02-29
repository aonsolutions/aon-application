package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnabort;
import com.esferalia.aon.watson.j2html.tags.attributes.IOncanplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class EmbedTag extends EmptyTag<EmbedTag>
    implements IHeight<EmbedTag>, IOnabort<EmbedTag>, IOncanplay<EmbedTag>, IOnerror<EmbedTag>, ISrc<EmbedTag>, IType<EmbedTag>, IWidth<EmbedTag> {
    public EmbedTag() {
        super("embed");
    }
}
