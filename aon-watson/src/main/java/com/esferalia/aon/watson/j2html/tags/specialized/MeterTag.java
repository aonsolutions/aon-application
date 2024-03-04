package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IForm;
import com.esferalia.aon.watson.j2html.tags.attributes.IHigh;
import com.esferalia.aon.watson.j2html.tags.attributes.ILow;
import com.esferalia.aon.watson.j2html.tags.attributes.IMax;
import com.esferalia.aon.watson.j2html.tags.attributes.IMin;
import com.esferalia.aon.watson.j2html.tags.attributes.IOptimum;
import com.esferalia.aon.watson.j2html.tags.attributes.IValue;

public final class MeterTag extends ContainerTag<MeterTag>
    implements IForm<MeterTag>, IHigh<MeterTag>, ILow<MeterTag>, IMax<MeterTag>, IMin<MeterTag>, IOptimum<MeterTag>, IValue<MeterTag> {
    public MeterTag() {
        super("meter");
    }
}
