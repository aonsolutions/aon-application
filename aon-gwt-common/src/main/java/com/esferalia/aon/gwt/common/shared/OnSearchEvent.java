package com.esferalia.aon.gwt.common.shared;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class OnSearchEvent extends GwtEvent<OnSearchEvent.Handler> {
    
    public interface Handler extends EventHandler {
        void onMyChange(OnSearchEvent event);
    }

    public static final Type<Handler> TYPE = new Type<>();

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        handler.onMyChange(this);
    }
}