/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired when value changes.<br>To comply with <a href="https://developer.mozilla.org/en-US/docs/Web/Events/change">https://developer.mozilla.org/en-US/docs/Web/Events/change</a></p>
 */
public class ChangeEvent extends DomEvent<ChangeEventHandler> {

    public static Type<ChangeEventHandler> TYPE = new Type<ChangeEventHandler>(
       net.aonsolutions.polymer.aon.event.ChangeEvent.NAME, new ChangeEvent());


    public ChangeEvent() {
    }

    public Type<ChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeEventHandler handler) {
        handler.onChange(this);
    }

    public net.aonsolutions.polymer.aon.event.ChangeEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.ChangeEvent)super.getNativeEvent();
    }


}
