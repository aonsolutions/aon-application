package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnafterprint;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnbeforeprint;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnbeforeunload;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnhashchange;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnoffline;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnonline;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnpagehide;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnpageshow;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnpopstate;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnresize;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnstorage;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnunload;

public final class BodyTag extends ContainerTag<BodyTag>
        implements
            IOnafterprint<BodyTag>,
            IOnbeforeprint<BodyTag>,
            IOnbeforeunload<BodyTag>,
            IOnerror<BodyTag>,
            IOnhashchange<BodyTag>,
            IOnload<BodyTag>,
            IOnoffline<BodyTag>,
            IOnonline<BodyTag>,
            IOnpagehide<BodyTag>,
            IOnpageshow<BodyTag>,
            IOnpopstate<BodyTag>,
            IOnresize<BodyTag>,
            IOnstorage<BodyTag>,
            IOnunload<BodyTag> {

    public BodyTag() {
        super("body");
    }
}
