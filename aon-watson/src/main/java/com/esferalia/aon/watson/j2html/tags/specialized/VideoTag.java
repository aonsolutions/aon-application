package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutoplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IControls;
import com.esferalia.aon.watson.j2html.tags.attributes.IHeight;
import com.esferalia.aon.watson.j2html.tags.attributes.ILoop;
import com.esferalia.aon.watson.j2html.tags.attributes.IMuted;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnabort;
import com.esferalia.aon.watson.j2html.tags.attributes.IOncanplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IOncanplaythrough;
import com.esferalia.aon.watson.j2html.tags.attributes.IOndurationchange;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnemptied;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnended;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnerror;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnloadeddata;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnloadedmetadata;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnloadstart;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnpause;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnplaying;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnprogress;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnratechange;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnseeked;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnseeking;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnstalled;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnsuspend;
import com.esferalia.aon.watson.j2html.tags.attributes.IOntimeupdate;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnvolumechanged;
import com.esferalia.aon.watson.j2html.tags.attributes.IOnwaiting;
import com.esferalia.aon.watson.j2html.tags.attributes.IPoster;
import com.esferalia.aon.watson.j2html.tags.attributes.IPreload;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;
import com.esferalia.aon.watson.j2html.tags.attributes.IWidth;

public final class VideoTag extends ContainerTag<VideoTag>
    implements IAutoplay<VideoTag>, IControls<VideoTag>, IHeight<VideoTag>, ILoop<VideoTag>, IMuted<VideoTag>, IOnabort<VideoTag>, IOncanplay<VideoTag>, IOncanplaythrough<VideoTag>, IOndurationchange<VideoTag>, IOnemptied<VideoTag>, IOnended<VideoTag>, IOnerror<VideoTag>, IOnloadeddata<VideoTag>, IOnloadedmetadata<VideoTag>, IOnloadstart<VideoTag>, IOnpause<VideoTag>, IOnplay<VideoTag>, IOnplaying<VideoTag>, IOnprogress<VideoTag>, IOnratechange<VideoTag>, IOnseeked<VideoTag>, IOnseeking<VideoTag>, IOnstalled<VideoTag>, IOnsuspend<VideoTag>, IOntimeupdate<VideoTag>, IOnvolumechanged<VideoTag>, IOnwaiting<VideoTag>, IPoster<VideoTag>, IPreload<VideoTag>, ISrc<VideoTag>, IWidth<VideoTag> {
    public VideoTag() {
        super("video");
    }
}
