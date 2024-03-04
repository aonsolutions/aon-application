package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAction;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutocomplete;
import com.esferalia.aon.watson.j2html.tags.attributes.IEnctype;
import com.esferalia.aon.watson.j2html.tags.attributes.IMethod;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.INovalidate;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnreset;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnsubmit;
import com.esferalia.aon.watson.j2html.tags.attributes.IRel;
import com.esferalia.aon.watson.j2html.tags.attributes.ITarget;

public final class FormTag extends ContainerTag<FormTag>
    implements IAction<FormTag>, IAutocomplete<FormTag>, IEnctype<FormTag>, IMethod<FormTag>, IName<FormTag>, INovalidate<FormTag>, IOnreset<FormTag>, IOnsubmit<FormTag>, IRel<FormTag>, ITarget<FormTag> {
    public FormTag() {
        super("form");
    }
}
