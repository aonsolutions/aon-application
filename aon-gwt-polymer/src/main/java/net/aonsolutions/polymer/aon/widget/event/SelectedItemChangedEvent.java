/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired when selected item changes.</p>
 */
public class SelectedItemChangedEvent extends DomEvent<SelectedItemChangedEventHandler> {

    public static Type<SelectedItemChangedEventHandler> TYPE = new Type<SelectedItemChangedEventHandler>(
       net.aonsolutions.polymer.aon.event.SelectedItemChangedEvent.NAME, new SelectedItemChangedEvent());


    public SelectedItemChangedEvent() {
    }

    public Type<SelectedItemChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SelectedItemChangedEventHandler handler) {
        handler.onSelectedItemChanged(this);
    }

    public net.aonsolutions.polymer.aon.event.SelectedItemChangedEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.SelectedItemChangedEvent)super.getNativeEvent();
    }


    /**
     * 
     */
    public JavaScriptObject getDetail() {
        return getPolymerEvent().getDetail().getDetail();
    }

    /**
     * <p>the selected item. Type is the same as the type of <code>items</code>.</p>
     */
    public Object getValue() {
        return getPolymerEvent().getDetail().getValue();
    }

}
