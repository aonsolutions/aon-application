package com.esferalia.aon.watson.j2html.tags.specialized;

import com.esferalia.aon.watson.j2html.tags.ContainerTag;
import com.esferalia.aon.watson.j2html.tags.attributes.IAutoplay;
import com.esferalia.aon.watson.j2html.tags.attributes.IControls;
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
import com.esferalia.aon.watson.j2html.tags.attributes.IPreload;
import com.esferalia.aon.watson.j2html.tags.attributes.ISrc;

public final class AudioTag extends ContainerTag<AudioTag>
    implements IAutoplay<AudioTag>, IControls<AudioTag>, ILoop<AudioTag>, IMuted<AudioTag>, IOnabort<AudioTag>, IOncanplay<AudioTag>, IOncanplaythrough<AudioTag>, IOndurationchange<AudioTag>, IOnemptied<AudioTag>, IOnended<AudioTag>, IOnerror<AudioTag>, IOnloadeddata<AudioTag>, IOnloadedmetadata<AudioTag>, IOnloadstart<AudioTag>, IOnpause<AudioTag>, IOnplay<AudioTag>, IOnplaying<AudioTag>, IOnprogress<AudioTag>, IOnratechange<AudioTag>, IOnseeked<AudioTag>, IOnseeking<AudioTag>, IOnstalled<AudioTag>, IOnsuspend<AudioTag>, IOntimeupdate<AudioTag>, IOnvolumechanged<AudioTag>, IOnwaiting<AudioTag>, IPreload<AudioTag>, ISrc<AudioTag> {
    public AudioTag() {
        super("audio");
    }
}
