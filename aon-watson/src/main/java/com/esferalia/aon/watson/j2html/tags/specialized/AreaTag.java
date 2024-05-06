package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAlt;
import com.esferalia.aon.watson.j2html.tags.attributes.ICoords;
import com.esferalia.aon.watson.j2html.tags.attributes.IDownload;
import com.esferalia.aon.watson.j2html.tags.attributes.IHref;
import com.esferalia.aon.watson.j2html.tags.attributes.IHreflang;
import com.esferalia.aon.watson.j2html.tags.attributes.IMedia;
import com.esferalia.aon.watson.j2html.tags.attributes.IRel;
import com.esferalia.aon.watson.j2html.tags.attributes.IShape;
import com.esferalia.aon.watson.j2html.tags.attributes.ITarget;

public final class AreaTag extends EmptyTag<AreaTag>
    implements IAlt<AreaTag>, ICoords<AreaTag>, IDownload<AreaTag>, IHref<AreaTag>, IHreflang<AreaTag>, IMedia<AreaTag>, IRel<AreaTag>, IShape<AreaTag>, ITarget<AreaTag> {
    public AreaTag() {
        super("area");
    }
}
