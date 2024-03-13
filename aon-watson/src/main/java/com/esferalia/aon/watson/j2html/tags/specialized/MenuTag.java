package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class MenuTag extends ContainerTag<MenuTag>
    implements IType<MenuTag> {
    public MenuTag() {
        super("menu");
    }
}
