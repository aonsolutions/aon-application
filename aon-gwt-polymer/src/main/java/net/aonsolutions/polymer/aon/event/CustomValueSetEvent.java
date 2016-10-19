/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.event;

import com.vaadin.polymer.elemental.Event;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * <p>Fired when the user sets a custom value.</p>
 */
@JsType(isNative=true)
public interface CustomValueSetEvent extends Event {

    @JsOverlay static final String NAME = "custom-value-set";

    @Override
    @JsProperty
    Detail getDetail();

    @JsType(isNative=true)
    interface Detail extends Event.Detail {

        /**
         * <p>the custom value</p>
         */
        @JsProperty String getDetail();

    }

}
