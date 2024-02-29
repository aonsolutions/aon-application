package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAlt;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.IIsmap;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnabort;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.ISizes;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrcset;
import com.esferalia.aon.watson.j2html.tags.attributes.IUsemap;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class ImgTag extends EmptyTag<ImgTag>
    implements IAlt<ImgTag>, IHeight<ImgTag>, IIsmap<ImgTag>, IOnabort<ImgTag>, IOnerror<ImgTag>, IOnload<ImgTag>, ISizes<ImgTag>, ISrc<ImgTag>, ISrcset<ImgTag>, IUsemap<ImgTag>, IWidth<ImgTag> {
    public ImgTag() {
        super("img");
    }
}
