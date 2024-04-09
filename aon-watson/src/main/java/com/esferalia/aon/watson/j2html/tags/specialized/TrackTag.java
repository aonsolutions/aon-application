package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.EmptyTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IDefault;
import com.esferalia.aon.watson.j2html.tags.attributes.IKind;
import com.esferalia.aon.watson.j2html.tags.attributes.ILabel;
import com.esferalia.aon.watson.j2html.tags.attributes.IOncuechange;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrclang;

public final class TrackTag extends EmptyTag<TrackTag>
    implements IDefault<TrackTag>, IKind<TrackTag>, ILabel<TrackTag>, IOncuechange<TrackTag>, ISrc<TrackTag>, ISrclang<TrackTag> {
    public TrackTag() {
        super("track");
    }
}
