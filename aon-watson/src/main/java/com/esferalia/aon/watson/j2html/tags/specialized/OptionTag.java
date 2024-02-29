package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDisabled;
import com.esferalia.aon.watson.j2html.tags.attributes.ILabel;
import com.esferalia.aon.watson.j2html.tags.attributes.ISelected;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class OptionTag extends ContainerTag<OptionTag>
    implements IDisabled<OptionTag>, ILabel<OptionTag>, ISelected<OptionTag>, IValue<OptionTag> {
    public OptionTag() {
        super("option");
    }
}
