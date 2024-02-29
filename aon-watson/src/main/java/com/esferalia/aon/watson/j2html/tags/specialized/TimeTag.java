package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDatetime;

public final class TimeTag extends ContainerTag<TimeTag>
    implements IDatetime<TimeTag> {
    public TimeTag() {
        super("time");
    }
}
