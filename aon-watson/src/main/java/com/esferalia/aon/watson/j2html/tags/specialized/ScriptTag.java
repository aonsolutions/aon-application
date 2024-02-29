package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAsync;
import com.esferalia.aon.watson.j2html.tags.attributes.ICharset;
import com.esferalia.aon.watson.j2html.tags.attributes.IDefer;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnload;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.IType;

public final class ScriptTag extends ContainerTag<ScriptTag>
    implements IAsync<ScriptTag>, ICharset<ScriptTag>, IDefer<ScriptTag>, IOnerror<ScriptTag>, IOnload<ScriptTag>, ISrc<ScriptTag>, IType<ScriptTag> {
    public ScriptTag() {
        super("script");
    }
}
