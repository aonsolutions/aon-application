package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IData;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnabort;
import com.esferalia.aon.watson.j2html.tags.attributes.IOncanplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;
import com.esferalia.aon.watson.j2html.tags.attributes.IUsemap;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class ObjectTag extends ContainerTag<ObjectTag>
    implements IData<ObjectTag>, IForm<ObjectTag>, IHeight<ObjectTag>, IName<ObjectTag>, IOnabort<ObjectTag>, IOncanplay<ObjectTag>, IOnerror<ObjectTag>, IType<ObjectTag>, IUsemap<ObjectTag>, IWidth<ObjectTag> {
    public ObjectTag() {
        super("object");
    }
}
