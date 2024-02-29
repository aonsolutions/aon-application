package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDownload;
import com.esferalia.aon.watson.j2html.tags.attributes.IHref;
import com.esferalia.aon.watson.j2html.tags.attributes.IHreflang;
import com.esferalia.aon.watson.j2html.tags.attributes.IMedia;
import com.esferalia.aon.watson.j2html.tags.attributes.IRel;
import com.esferalia.aon.watson.j2html.tags.attributes.ITarget;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class ATag extends ContainerTag<ATag>
    implements IDownload<ATag>, IHref<ATag>, IHreflang<ATag>, IMedia<ATag>, IRel<ATag>, ITarget<ATag>, IType<ATag> {
    public ATag() {
        super("a");
    }
}
