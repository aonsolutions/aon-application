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
 * <p>Fired when selected item changes.</p>
 */
@JsType(isNative=true)
public interface SelectedItemChangedEvent extends Event {

    @JsOverlay static final String NAME = "selected-item-changed";

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
         * <p>the selected item. Type is the same as the type of <code>items</code>.</p>
         */
        @JsProperty Object getValue();

    }

}
