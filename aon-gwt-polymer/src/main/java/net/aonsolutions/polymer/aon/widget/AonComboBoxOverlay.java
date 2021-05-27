/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.PolymerWidget;

import net.aonsolutions.polymer.aon.AonComboBoxOverlayElement;

/**
 * 
 */
public class AonComboBoxOverlay extends PolymerWidget {
    /**
     * Default Constructor.
     */
    public AonComboBoxOverlay() {
       this("");
    }

    /**
     * Constructor used by UIBinder to create widgets with content.
     */
    public AonComboBoxOverlay(String html) {
        super(AonComboBoxOverlayElement.TAG, AonComboBoxOverlayElement.SRC, html);
    }

    /**
     * Gets a handle to the Polymer object's underlying DOM element.
     */
    public AonComboBoxOverlayElement getPolymerElement() {
        try {
            return (AonComboBoxOverlayElement) getElement();
        } catch (ClassCastException e) {
            jsinteropError();
            return null;
        }
    }


    /**
     * <p>The element to position/align the dropdown by.</p>
     *
     * JavaScript Info:
     * @property positionTarget
     * @type Object
     * 
     */
    public JavaScriptObject getPositionTarget() {
        return getPolymerElement().getPositionTarget();
    }
    /**
     * <p>The element to position/align the dropdown by.</p>
     *
     * JavaScript Info:
     * @property positionTarget
     * @type Object
     * 
     */
    public void setPositionTarget(JavaScriptObject value) {
        getPolymerElement().setPositionTarget(value);
    }

    /**
     * <p>True if the device supports touch events.</p>
     *
     * JavaScript Info:
     * @property touchDevice
     * @type Boolean
     * 
     */
    public boolean getTouchDevice() {
        return getPolymerElement().getTouchDevice();
    }
    /**
     * <p>True if the device supports touch events.</p>
     *
     * JavaScript Info:
     * @property touchDevice
     * @type Boolean
     * 
     */
    public void setTouchDevice(boolean value) {
        getPolymerElement().setTouchDevice(value);
    }

    /**
     * <p>Vertical offset for the overlay position.</p>
     *
     * JavaScript Info:
     * @property verticalOffset
     * @type Number
     * 
     */
    public double getVerticalOffset() {
        return getPolymerElement().getVerticalOffset();
    }
    /**
     * <p>Vertical offset for the overlay position.</p>
     *
     * JavaScript Info:
     * @property verticalOffset
     * @type Number
     * 
     */
    public void setVerticalOffset(double value) {
        getPolymerElement().setVerticalOffset(value);
    }


    // Needed in UIBinder
    /**
     * <p>Vertical offset for the overlay position.</p>
     *
     * JavaScript Info:
     * @attribute vertical-offset
     * 
     */
    public void setVerticalOffset(String value) {
        Polymer.property(this, "vertical-offset", value);
        // getPolymerElement().setAttribute("vertical-offset", value);
    }

    // Needed in UIBinder
    /**
     * <p>The element to position/align the dropdown by.</p>
     *
     * JavaScript Info:
     * @attribute position-target
     * @behavior AonComboBoxOverlay
     */
    public void setPositionTarget(String value) {
        Polymer.property(this, "position-target", value);
        // getPolymerElement().setAttribute("position-target", value);
    }


    /**
     * <p>Gets the index of the item with the provided label.</p>
     *
     * JavaScript Info:
     * @method indexOfLabel
     * @param {} label  
     * 
     * @return {double}
     */
    public double indexOfLabel(Object label) {
        return getPolymerElement().indexOfLabel(label);
    }

    /**
     * <p>Gets the label string for the item based on the <code>_itemLabelPath</code>.</p>
     *
     * JavaScript Info:
     * @method getItemLabel
     * @param {} item  
     * 
     * @return {String}
     */
    public String getItemLabel(Object item) {
        return getPolymerElement().getItemLabel(item);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @method ensureItemsRendered
     * 
     * 
     */
    public void ensureItemsRendered() {
        getPolymerElement().ensureItemsRendered();
    }

    /**
     * 
     *
     * JavaScript Info:
     * @method updateViewportBoundaries
     * 
     * 
     */
    public void updateViewportBoundaries() {
        getPolymerElement().updateViewportBoundaries();
    }

    /**
     * 
     *
     * JavaScript Info:
     * @method adjustScrollPosition
     * 
     * 
     */
    public void adjustScrollPosition() {
        getPolymerElement().adjustScrollPosition();
    }


}
