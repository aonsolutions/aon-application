package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutofocus;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IMultiple;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IRequired;
import com.esferalia.aon.watson.j2html.tags.attributes.ISize;

public final class SelectTag extends ContainerTag<SelectTag>
    implements IAutofocus<SelectTag>, IDisabled<SelectTag>, IForm<SelectTag>, IMultiple<SelectTag>, IName<SelectTag>, IRequired<SelectTag>, ISize<SelectTag> {
    public SelectTag() {
        super("select");
    }
}
