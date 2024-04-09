package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IMedia;
import com.esferalia.aon.watson.j2html.tags.attributes.ISizes;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrcset;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class SourceTag extends EmptyTag<SourceTag>
    implements IMedia<SourceTag>, ISizes<SourceTag>, ISrc<SourceTag>, ISrcset<SourceTag>, IType<SourceTag> {
    public SourceTag() {
        super("source");
    }
}
