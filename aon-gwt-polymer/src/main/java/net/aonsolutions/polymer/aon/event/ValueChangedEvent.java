/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.event;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.polymer.elemental.Event;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * <p>Fired when the value changes.</p>
 */
@JsType(isNative=true)
public interface ValueChangedEvent extends Event {

    @JsOverlay static final String NAME = "value-changed";

    @Override
    @JsProperty
    Detail getDetail();

    @JsType(isNative=true)
    interface Detail extends Event.Detail {

        /**
         * 
         */
        @JsProperty JavaScriptObject getDetail();

        /**
         * <p>the combobox value</p>
         */
        @JsProperty String getValue();

    }

}
