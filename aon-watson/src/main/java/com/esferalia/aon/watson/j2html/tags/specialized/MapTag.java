package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IName;

public final class MapTag extends ContainerTag<MapTag>
    implements IName<MapTag> {
    public MapTag() {
        super("map");
    }
}
