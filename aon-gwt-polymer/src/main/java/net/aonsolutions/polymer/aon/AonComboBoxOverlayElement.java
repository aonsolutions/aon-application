/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.polymer.elemental.HTMLElement;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * 
 */
@JsType(isNative=true)
public interface AonComboBoxOverlayElement extends HTMLElement {

    @JsOverlay public static final String TAG = "aon-combo-box-overlay";
    @JsOverlay public static final String SRC = "aon-combo-box/doc-imports.html";


    /**
     * <p>The element to position/align the dropdown by.</p>
     *
     * JavaScript Info:
     * @property positionTarget
     * @type Object
     * 
     */
    @JsProperty JavaScriptObject getPositionTarget();
    /**
     * <p>The element to position/align the dropdown by.</p>
     *
     * JavaScript Info:
     * @property positionTarget
     * @type Object
     * 
     */
    @JsProperty void setPositionTarget(JavaScriptObject value);

    /**
     * <p>True if the device supports touch events.</p>
     *
     * JavaScript Info:
     * @property touchDevice
     * @type Boolean
     * 
     */
    @JsProperty boolean getTouchDevice();
    /**
     * <p>True if the device supports touch events.</p>
     *
     * JavaScript Info:
     * @property touchDevice
     * @type Boolean
     * 
     */
    @JsProperty void setTouchDevice(boolean value);

    /**
     * <p>Vertical offset for the overlay position.</p>
     *
     * JavaScript Info:
     * @property verticalOffset
     * @type Number
     * 
     */
    @JsProperty double getVerticalOffset();
    /**
     * <p>Vertical offset for the overlay position.</p>
     *
     * JavaScript Info:
     * @property verticalOffset
     * @type Number
     * 
     */
    @JsProperty void setVerticalOffset(double value);


    /**
     * <p>Gets the index of the item with the provided label.</p>
     *
     * JavaScript Info:
     * @method indexOfLabel
     * @param {} label  
     * 
     * @return {double}
     */
    double indexOfLabel(Object label);

    /**
     * <p>Gets the label string for the item based on the <code>_itemLabelPath</code>.</p>
     *
     * JavaScript Info:
     * @method getItemLabel
     * @param {} item  
     * 
     * @return {String}
     */
    String getItemLabel(Object item);

    /**
     * 
     *
     * JavaScript Info:
     * @method updateViewportBoundaries
     * 
     * 
     */
    void updateViewportBoundaries();

    /**
     * 
     *
     * JavaScript Info:
     * @method adjustScrollPosition
     * 
     * 
     */
    void adjustScrollPosition();

    /**
     * 
     *
     * JavaScript Info:
     * @method ensureItemsRendered
     * 
     * 
     */
    void ensureItemsRendered();

}
