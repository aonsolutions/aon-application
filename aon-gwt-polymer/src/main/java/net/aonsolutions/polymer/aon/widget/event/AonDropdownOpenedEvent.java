/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired after the <code>aon-dropdown</code> opens.</p>
 */
public class AonDropdownOpenedEvent extends DomEvent<AonDropdownOpenedEventHandler> {

    public static Type<AonDropdownOpenedEventHandler> TYPE = new Type<AonDropdownOpenedEventHandler>(
       net.aonsolutions.polymer.aon.event.AonDropdownOpenedEvent.NAME, new AonDropdownOpenedEvent());


    public AonDropdownOpenedEvent() {
    }

    public Type<AonDropdownOpenedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AonDropdownOpenedEventHandler handler) {
        handler.onAonDropdownOpened(this);
    }

    public net.aonsolutions.polymer.aon.event.AonDropdownOpenedEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.AonDropdownOpenedEvent)super.getNativeEvent();
    }


}
