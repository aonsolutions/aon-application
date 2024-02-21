package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;

public final class FieldsetTag extends ContainerTag<FieldsetTag>
    implements IDisabled<FieldsetTag>, IForm<FieldsetTag>, IName<FieldsetTag> {
    public FieldsetTag() {
        super("fieldset");
    }
}
