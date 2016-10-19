/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon.widget;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.shared.HandlerRegistration;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.PolymerWidget;

import net.aonsolutions.polymer.aon.AonComboBoxLightElement;
import net.aonsolutions.polymer.aon.widget.event.AonDropdownClosedEvent;
import net.aonsolutions.polymer.aon.widget.event.AonDropdownClosedEventHandler;
import net.aonsolutions.polymer.aon.widget.event.AonDropdownOpenedEvent;
import net.aonsolutions.polymer.aon.widget.event.AonDropdownOpenedEventHandler;
import net.aonsolutions.polymer.aon.widget.event.ChangeEvent;
import net.aonsolutions.polymer.aon.widget.event.ChangeEventHandler;
import net.aonsolutions.polymer.aon.widget.event.CustomValueSetEvent;
import net.aonsolutions.polymer.aon.widget.event.CustomValueSetEventHandler;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.SelectedItemChangedEventHandler;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEvent;
import net.aonsolutions.polymer.aon.widget.event.ValueChangedEventHandler;

/**
 * 
 */
public class AonComboBoxLight extends PolymerWidget {
    /**
     * Default Constructor.
     */
    public AonComboBoxLight() {
       this("");
    }

    /**
     * Constructor used by UIBinder to create widgets with content.
     */
    public AonComboBoxLight(String html) {
        super(AonComboBoxLightElement.TAG, AonComboBoxLightElement.SRC, html);
    }

    /**
     * Gets a handle to the Polymer object's underlying DOM element.
     */
    public AonComboBoxLightElement getPolymerElement() {
        try {
            return (AonComboBoxLightElement) getElement();
        } catch (ClassCastException e) {
            jsinteropError();
            return null;
        }
    }


    /**
     * <p>If <code>true</code>, the user can input a value that is not present in the items list.<br><code>value</code> property will be set to the input value in this case.<br>Also, when <code>value</code> is set programmatically, the input value will be set<br>to reflect that value.</p>
     *
     * JavaScript Info:
     * @property allowCustomValue
     * @type Boolean
     * 
     */
    public boolean getAllowCustomValue() {
        return getPolymerElement().getAllowCustomValue();
    }
    /**
     * <p>If <code>true</code>, the user can input a value that is not present in the items list.<br><code>value</code> property will be set to the input value in this case.<br>Also, when <code>value</code> is set programmatically, the input value will be set<br>to reflect that value.</p>
     *
     * JavaScript Info:
     * @property allowCustomValue
     * @type Boolean
     * 
     */
    public void setAllowCustomValue(boolean value) {
        getPolymerElement().setAllowCustomValue(value);
    }

    /**
     * <p>Returns a reference to the input element.</p>
     *
     * JavaScript Info:
     * @property inputElement
     * @type HTMLElement
     * 
     */
    public JavaScriptObject getInputElement() {
        return getPolymerElement().getInputElement();
    }
    /**
     * <p>Returns a reference to the input element.</p>
     *
     * JavaScript Info:
     * @property inputElement
     * @type HTMLElement
     * 
     */
    public void setInputElement(JavaScriptObject value) {
        getPolymerElement().setInputElement(value);
    }

    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @property items
     * @type Array
     * 
     */
    public JsArray getItems() {
        return getPolymerElement().getItems();
    }
    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @property items
     * @type Array
     * 
     */
    public void setItems(JsArray value) {
        getPolymerElement().setItems(value);
    }

    /**
     * <p>Number of pixels used as the vertical offset in positioning of<br>the dropdown.</p>
     *
     * JavaScript Info:
     * @property overlayVerticalOffset
     * @type Number
     * 
     */
    public double getOverlayVerticalOffset() {
        return getPolymerElement().getOverlayVerticalOffset();
    }
    /**
     * <p>Number of pixels used as the vertical offset in positioning of<br>the dropdown.</p>
     *
     * JavaScript Info:
     * @property overlayVerticalOffset
     * @type Number
     * 
     */
    public void setOverlayVerticalOffset(double value) {
        getPolymerElement().setOverlayVerticalOffset(value);
    }

    /**
     * <p>The selected item from the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property selectedItem
     * @type Object
     * @behavior AonComboBoxLight
     */
    public JavaScriptObject getSelectedItem() {
        return getPolymerElement().getSelectedItem();
    }
    /**
     * <p>The selected item from the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property selectedItem
     * @type Object
     * @behavior AonComboBoxLight
     */
    public void setSelectedItem(JavaScriptObject value) {
        getPolymerElement().setSelectedItem(value);
    }

    /**
     * <p>True if the dropdown is open, false otherwise.</p>
     *
     * JavaScript Info:
     * @property opened
     * @type Boolean
     * 
     */
    public boolean getOpened() {
        return getPolymerElement().getOpened();
    }
    /**
     * <p>True if the dropdown is open, false otherwise.</p>
     *
     * JavaScript Info:
     * @property opened
     * @type Boolean
     * 
     */
    public void setOpened(boolean value) {
        getPolymerElement().setOpened(value);
    }

    /**
     * <p>A read-only property indicating whether this combo box has a value<br>selected or not. It can be used for example in styling of the component.</p>
     *
     * JavaScript Info:
     * @property hasValue
     * @type Boolean
     * 
     */
    public boolean getHasValue() {
        return getPolymerElement().getHasValue();
    }
    /**
     * <p>A read-only property indicating whether this combo box has a value<br>selected or not. It can be used for example in styling of the component.</p>
     *
     * JavaScript Info:
     * @property hasValue
     * @type Boolean
     * 
     */
    public void setHasValue(boolean value) {
        getPolymerElement().setHasValue(value);
    }

    /**
     * <p>When present, it specifies that the element field is read-only.</p>
     *
     * JavaScript Info:
     * @property readonly
     * @type Boolean
     * 
     */
    public boolean getReadonly() {
        return getPolymerElement().getReadonly();
    }
    /**
     * <p>When present, it specifies that the element field is read-only.</p>
     *
     * JavaScript Info:
     * @property readonly
     * @type Boolean
     * 
     */
    public void setReadonly(boolean value) {
        getPolymerElement().setReadonly(value);
    }

    /**
     * <p>Set to true to disable this element.</p>
     *
     * JavaScript Info:
     * @property disabled
     * @type Boolean
     * 
     */
    public boolean getDisabled() {
        return getPolymerElement().getDisabled();
    }
    /**
     * <p>Set to true to disable this element.</p>
     *
     * JavaScript Info:
     * @property disabled
     * @type Boolean
     * 
     */
    public void setDisabled(boolean value) {
        getPolymerElement().setDisabled(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property filterEnable
     * @type Boolean
     * @behavior AonComboBoxLight
     */
    public boolean getFilterEnable() {
        return getPolymerElement().getFilterEnable();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property filterEnable
     * @type Boolean
     * @behavior AonComboBoxLight
     */
    public void setFilterEnable(boolean value) {
        getPolymerElement().setFilterEnable(value);
    }

    /**
     * <p>Path for the value of the item. If <code>items</code> is an array of objects, the<br><code>itemValuePath:</code> is used to fetch the string value for the selected<br>item.</p>
     * <p>The item value is used in the <code>value</code> property of the combo box,<br>to provide the form value.</p>
     *
     * JavaScript Info:
     * @property itemValuePath
     * @type String
     * @behavior AonComboBoxLight
     */
    public String getItemValuePath() {
        return getPolymerElement().getItemValuePath();
    }
    /**
     * <p>Path for the value of the item. If <code>items</code> is an array of objects, the<br><code>itemValuePath:</code> is used to fetch the string value for the selected<br>item.</p>
     * <p>The item value is used in the <code>value</code> property of the combo box,<br>to provide the form value.</p>
     *
     * JavaScript Info:
     * @property itemValuePath
     * @type String
     * @behavior AonComboBoxLight
     */
    public void setItemValuePath(String value) {
        getPolymerElement().setItemValuePath(value);
    }

    /**
     * <p>The <code>String</code> value for the selected item of the combo box. Provides<br>the value for <code>iron-form</code>.</p>
     * <p>When there’s no item selected, the value is an empty string.</p>
     * <p>Use <code>selectedItem</code> property to get the raw selected item from<br>the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property value
     * @type String
     * @behavior AonComboBoxLight
     */
    public String getValue() {
        return getPolymerElement().getValue();
    }
    /**
     * <p>The <code>String</code> value for the selected item of the combo box. Provides<br>the value for <code>iron-form</code>.</p>
     * <p>When there’s no item selected, the value is an empty string.</p>
     * <p>Use <code>selectedItem</code> property to get the raw selected item from<br>the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property value
     * @type String
     * @behavior AonComboBoxLight
     */
    public void setValue(String value) {
        getPolymerElement().setValue(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property inputElementValue
     * @type String
     * @behavior AonComboBoxLight
     */
    public String getInputElementValue() {
        return getPolymerElement().getInputElementValue();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property inputElementValue
     * @type String
     * @behavior AonComboBoxLight
     */
    public void setInputElementValue(String value) {
        getPolymerElement().setInputElementValue(value);
    }

    /**
     * <p>Name of the two-way data-bindable property representing the<br>value of the custom input field.</p>
     *
     * JavaScript Info:
     * @property attrForValue
     * @type String
     * 
     */
    public String getAttrForValue() {
        return getPolymerElement().getAttrForValue();
    }
    /**
     * <p>Name of the two-way data-bindable property representing the<br>value of the custom input field.</p>
     *
     * JavaScript Info:
     * @property attrForValue
     * @type String
     * 
     */
    public void setAttrForValue(String value) {
        getPolymerElement().setAttrForValue(value);
    }

    /**
     * <p>Path for label of the item. If <code>items</code> is an array of objects, the<br><code>itemLabelPath</code> is used to fetch the displayed string label for each<br>item.</p>
     * <p>The item label is also used for matching items when processing user<br>input, i.e., for filtering and selecting items.</p>
     *
     * JavaScript Info:
     * @property itemLabelPath
     * @type String
     * @behavior AonComboBoxLight
     */
    public String getItemLabelPath() {
        return getPolymerElement().getItemLabelPath();
    }
    /**
     * <p>Path for label of the item. If <code>items</code> is an array of objects, the<br><code>itemLabelPath</code> is used to fetch the displayed string label for each<br>item.</p>
     * <p>The item label is also used for matching items when processing user<br>input, i.e., for filtering and selecting items.</p>
     *
     * JavaScript Info:
     * @property itemLabelPath
     * @type String
     * @behavior AonComboBoxLight
     */
    public void setItemLabelPath(String value) {
        getPolymerElement().setItemLabelPath(value);
    }


    // Needed in UIBinder
    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @attribute items
     * @behavior AonComboBoxLight
     */
    public void setItems(String value) {
        Polymer.property(this, "items", value);
        // getPolymerElement().setAttribute("items", value);
    }

    // Needed in UIBinder
    /**
     * <p>Number of pixels used as the vertical offset in positioning of<br>the dropdown.</p>
     *
     * JavaScript Info:
     * @attribute overlay-vertical-offset
     * 
     */
    public void setOverlayVerticalOffset(String value) {
        Polymer.property(this, "overlay-vertical-offset", value);
        // getPolymerElement().setAttribute("overlay-vertical-offset", value);
    }

    // Needed in UIBinder
    /**
     * <p>The selected item from the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @attribute selected-item
     * 
     */
    public void setSelectedItem(String value) {
        Polymer.property(this, "selected-item", value);
        // getPolymerElement().setAttribute("selected-item", value);
    }

    // Needed in UIBinder
    /**
     * <p>Returns a reference to the input element.</p>
     *
     * JavaScript Info:
     * @attribute input-element
     * @behavior AonComboBoxLight
     */
    public void setInputElement(String value) {
        Polymer.property(this, "input-element", value);
        // getPolymerElement().setAttribute("input-element", value);
    }


    /**
     * <p>Closes the dropdown list.</p>
     *
     * JavaScript Info:
     * @method close
     * @behavior AonComboBoxLight
     * 
     */
    public void close() {
        getPolymerElement().close();
    }

    /**
     * <p>Reverts back to original value.</p>
     *
     * JavaScript Info:
     * @method cancel
     * 
     * 
     */
    public void cancel() {
        getPolymerElement().cancel();
    }

    /**
     * <p>Opens the dropdown list.</p>
     *
     * JavaScript Info:
     * @method open
     * @behavior AonComboBoxLight
     * 
     */
    public void open() {
        getPolymerElement().open();
    }


    /**
     * <p>Fired after the <code>aon-dropdown</code> closes.</p>
     *
     * JavaScript Info:
     * @event aon-dropdown-closed
     */
    public HandlerRegistration addAonDropdownClosedHandler(AonDropdownClosedEventHandler handler) {
        return addDomHandler(handler, AonDropdownClosedEvent.TYPE);
    }

    /**
     * <p>Fired after the <code>aon-dropdown</code> opens.</p>
     *
     * JavaScript Info:
     * @event aon-dropdown-opened
     */
    public HandlerRegistration addAonDropdownOpenedHandler(AonDropdownOpenedEventHandler handler) {
        return addDomHandler(handler, AonDropdownOpenedEvent.TYPE);
    }

    /**
     * <p>Fired when value changes.<br>To comply with <a href="https://developer.mozilla.org/en-US/docs/Web/Events/change">https://developer.mozilla.org/en-US/docs/Web/Events/change</a></p>
     *
     * JavaScript Info:
     * @event change
     */
    public HandlerRegistration addChangeHandler(ChangeEventHandler handler) {
        return addDomHandler(handler, ChangeEvent.TYPE);
    }

    /**
     * <p>Fired when the user sets a custom value.</p>
     *
     * JavaScript Info:
     * @event custom-value-set
     */
    public HandlerRegistration addCustomValueSetHandler(CustomValueSetEventHandler handler) {
        return addDomHandler(handler, CustomValueSetEvent.TYPE);
    }

    /**
     * <p>Fired when selected item changes.</p>
     *
     * JavaScript Info:
     * @event selected-item-changed
     */
    public HandlerRegistration addSelectedItemChangedHandler(SelectedItemChangedEventHandler handler) {
        return addDomHandler(handler, SelectedItemChangedEvent.TYPE);
    }

    /**
     * <p>Fired when the value changes.</p>
     *
     * JavaScript Info:
     * @event value-changed
     */
    public HandlerRegistration addValueChangedHandler(ValueChangedEventHandler handler) {
        return addDomHandler(handler, ValueChangedEvent.TYPE);
    }

}
