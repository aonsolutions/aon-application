package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IMedia;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class StyleTag extends ContainerTag<StyleTag>
    implements IMedia<StyleTag>, IOnerror<StyleTag>, IOnload<StyleTag>, IType<StyleTag> {
    public StyleTag() {
        super("style");
    }
}
