/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired when the user sets a custom value.</p>
 */
public class CustomValueSetEvent extends DomEvent<CustomValueSetEventHandler> {

    public static Type<CustomValueSetEventHandler> TYPE = new Type<CustomValueSetEventHandler>(
       net.aonsolutions.polymer.aon.event.CustomValueSetEvent.NAME, new CustomValueSetEvent());


    public CustomValueSetEvent() {
    }

    public Type<CustomValueSetEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CustomValueSetEventHandler handler) {
        handler.onCustomValueSet(this);
    }

    public net.aonsolutions.polymer.aon.event.CustomValueSetEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.CustomValueSetEvent)super.getNativeEvent();
    }


    /**
     * <p>the custom value</p>
     */
    public String getDetail() {
        return getPolymerEvent().getDetail().getDetail();
    }

}
