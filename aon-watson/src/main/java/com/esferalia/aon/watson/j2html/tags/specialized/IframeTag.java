package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.ISandbox;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrcdoc;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class IframeTag extends ContainerTag<IframeTag>
    implements IHeight<IframeTag>, IName<IframeTag>, IOnload<IframeTag>, ISandbox<IframeTag>, ISrc<IframeTag>, ISrcdoc<IframeTag>, IWidth<IframeTag> {
    public IframeTag() {
        super("iframe");
    }
}
