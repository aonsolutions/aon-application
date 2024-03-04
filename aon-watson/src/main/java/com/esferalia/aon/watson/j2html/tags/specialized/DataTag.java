package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class DataTag extends ContainerTag<DataTag>
    implements IValue<DataTag> {
    public DataTag() {
        super("data");
    }
}
