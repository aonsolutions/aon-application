package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutofocus;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IFormaction;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class ButtonTag extends ContainerTag<ButtonTag>
    implements IAutofocus<ButtonTag>, IDisabled<ButtonTag>, IForm<ButtonTag>, IFormaction<ButtonTag>, IName<ButtonTag>, IType<ButtonTag>, IValue<ButtonTag> {
    public ButtonTag() {
        super("button");
    }
}
