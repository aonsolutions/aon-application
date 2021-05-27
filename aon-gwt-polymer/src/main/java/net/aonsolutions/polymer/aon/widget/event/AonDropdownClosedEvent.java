/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired after the <code>aon-dropdown</code> closes.</p>
 */
public class AonDropdownClosedEvent extends DomEvent<AonDropdownClosedEventHandler> {

    public static Type<AonDropdownClosedEventHandler> TYPE = new Type<AonDropdownClosedEventHandler>(
       net.aonsolutions.polymer.aon.event.AonDropdownClosedEvent.NAME, new AonDropdownClosedEvent());


    public AonDropdownClosedEvent() {
    }

    public Type<AonDropdownClosedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AonDropdownClosedEventHandler handler) {
        handler.onAonDropdownClosed(this);
    }

    public net.aonsolutions.polymer.aon.event.AonDropdownClosedEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.AonDropdownClosedEvent)super.getNativeEvent();
    }


}
