/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.DomEvent;

/**
 * <p>Fired when the value changes.</p>
 */
public class ValueChangedEvent extends DomEvent<ValueChangedEventHandler> {

    public static Type<ValueChangedEventHandler> TYPE = new Type<ValueChangedEventHandler>(
       net.aonsolutions.polymer.aon.event.ValueChangedEvent.NAME, new ValueChangedEvent());


    public ValueChangedEvent() {
    }

    public Type<ValueChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ValueChangedEventHandler handler) {
        handler.onValueChanged(this);
    }

    public net.aonsolutions.polymer.aon.event.ValueChangedEvent getPolymerEvent() {
        return (net.aonsolutions.polymer.aon.event.ValueChangedEvent)super.getNativeEvent();
    }


    /**
     * 
     */
    public JavaScriptObject getDetail() {
        return getPolymerEvent().getDetail().getDetail();
    }

    /**
     * <p>the combobox value</p>
     */
    public String getValue() {
        return getPolymerEvent().getDetail().getValue();
    }

}
