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

import net.aonsolutions.polymer.aon.AonComboBoxElement;
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
public class AonComboBox extends PolymerWidget {
    /**
     * Default Constructor.
     */
    public AonComboBox() {
       this("");
    }

    /**
     * Constructor used by UIBinder to create widgets with content.
     */
    public AonComboBox(String html) {
        super(AonComboBoxElement.TAG, AonComboBoxElement.SRC, html);
    }

    /**
     * Gets a handle to the Polymer object's underlying DOM element.
     */
    public AonComboBoxElement getPolymerElement() {
        try {
            return (AonComboBoxElement) getElement();
        } catch (ClassCastException e) {
            //jsinteropError();
            return null;
        }
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
     * <p>Set to true to disable this element.</p>
     *
     * JavaScript Info:
     * @property disabled
     * @type Boolean
     * @behavior AonComboBoxLight
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
     * @behavior AonComboBoxLight
     */
    public void setDisabled(boolean value) {
        getPolymerElement().setDisabled(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property autofocus
     * @type Boolean
     * 
     */
    public boolean getAutofocus() {
        return getPolymerElement().getAutofocus();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property autofocus
     * @type Boolean
     * 
     */
    public void setAutofocus(boolean value) {
        getPolymerElement().setAutofocus(value);
    }

    /**
     * <p>Set to true to auto-validate the input value.</p>
     *
     * JavaScript Info:
     * @property autoValidate
     * @type Boolean
     * 
     */
    public boolean getAutoValidate() {
        return getPolymerElement().getAutoValidate();
    }
    /**
     * <p>Set to true to auto-validate the input value.</p>
     *
     * JavaScript Info:
     * @property autoValidate
     * @type Boolean
     * 
     */
    public void setAutoValidate(boolean value) {
        getPolymerElement().setAutoValidate(value);
    }

    /**
     * <p>Set to true to disable the floating label.</p>
     *
     * JavaScript Info:
     * @property noLabelFloat
     * @type Boolean
     * 
     */
    public boolean getNoLabelFloat() {
        return getPolymerElement().getNoLabelFloat();
    }
    /**
     * <p>Set to true to disable the floating label.</p>
     *
     * JavaScript Info:
     * @property noLabelFloat
     * @type Boolean
     * 
     */
    public void setNoLabelFloat(boolean value) {
        getPolymerElement().setNoLabelFloat(value);
    }

    /**
     * <p>Set to true to always float the label.</p>
     *
     * JavaScript Info:
     * @property alwaysFloatLabel
     * @type Boolean
     * 
     */
    public boolean getAlwaysFloatLabel() {
        return getPolymerElement().getAlwaysFloatLabel();
    }
    /**
     * <p>Set to true to always float the label.</p>
     *
     * JavaScript Info:
     * @property alwaysFloatLabel
     * @type Boolean
     * 
     */
    public void setAlwaysFloatLabel(boolean value) {
        getPolymerElement().setAlwaysFloatLabel(value);
    }

    /**
     * <p>When present, it specifies that the element field is read-only.</p>
     *
     * JavaScript Info:
     * @property readonly
     * @type Boolean
     * @behavior AonComboBoxLight
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
     * @behavior AonComboBoxLight
     */
    public void setReadonly(boolean value) {
        getPolymerElement().setReadonly(value);
    }

    /**
     * <p>Set to true to prevent the user from entering invalid input.</p>
     *
     * JavaScript Info:
     * @property preventInvalidInput
     * @type Boolean
     * 
     */
    public boolean getPreventInvalidInput() {
        return getPolymerElement().getPreventInvalidInput();
    }
    /**
     * <p>Set to true to prevent the user from entering invalid input.</p>
     *
     * JavaScript Info:
     * @property preventInvalidInput
     * @type Boolean
     * 
     */
    public void setPreventInvalidInput(boolean value) {
        getPolymerElement().setPreventInvalidInput(value);
    }

    /**
     * <p>Set to true to mark the input as required.</p>
     *
     * JavaScript Info:
     * @property required
     * @type Boolean
     * 
     */
    public boolean getRequired() {
        return getPolymerElement().getRequired();
    }
    /**
     * <p>Set to true to mark the input as required.</p>
     *
     * JavaScript Info:
     * @property required
     * @type Boolean
     * 
     */
    public void setRequired(boolean value) {
        getPolymerElement().setRequired(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property filterEnable
     * @type Boolean
     * 
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
     * 
     */
    public void setFilterEnable(boolean value) {
        getPolymerElement().setFilterEnable(value);
    }

    /**
     * <p>A read-only property indicating whether this combo box has a value<br>selected or not. It can be used for example in styling of the component.</p>
     *
     * JavaScript Info:
     * @property hasValue
     * @type Boolean
     * @behavior AonComboBoxLight
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
     * @behavior AonComboBoxLight
     */
    public void setHasValue(boolean value) {
        getPolymerElement().setHasValue(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property size
     * @type Number
     * 
     */
    public double getSize() {
        return getPolymerElement().getSize();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property size
     * @type Number
     * 
     */
    public void setSize(double value) {
        getPolymerElement().setSize(value);
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
     * 
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
     * 
     */
    public void setValue(String value) {
        getPolymerElement().setValue(value);
    }

    /**
     * <p>Set this to specify the pattern allowed by <code>preventInvalidInput</code>.</p>
     *
     * JavaScript Info:
     * @property allowedPattern
     * @type String
     * 
     */
    public String getAllowedPattern() {
        return getPolymerElement().getAllowedPattern();
    }
    /**
     * <p>Set this to specify the pattern allowed by <code>preventInvalidInput</code>.</p>
     *
     * JavaScript Info:
     * @property allowedPattern
     * @type String
     * 
     */
    public void setAllowedPattern(String value) {
        getPolymerElement().setAllowedPattern(value);
    }

    /**
     * <p>A placeholder string in addition to the label. If this is set, the label will always float.</p>
     *
     * JavaScript Info:
     * @property placeholder
     * @type String
     * 
     */
    public String getPlaceholder() {
        return getPolymerElement().getPlaceholder();
    }
    /**
     * <p>A placeholder string in addition to the label. If this is set, the label will always float.</p>
     *
     * JavaScript Info:
     * @property placeholder
     * @type String
     * 
     */
    public void setPlaceholder(String value) {
        getPolymerElement().setPlaceholder(value);
    }

    /**
     * <p>A pattern to validate the <code>input</code> with.</p>
     *
     * JavaScript Info:
     * @property pattern
     * @type String
     * 
     */
    public String getPattern() {
        return getPolymerElement().getPattern();
    }
    /**
     * <p>A pattern to validate the <code>input</code> with.</p>
     *
     * JavaScript Info:
     * @property pattern
     * @type String
     * 
     */
    public void setPattern(String value) {
        getPolymerElement().setPattern(value);
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
     * <p>The label for this element.</p>
     *
     * JavaScript Info:
     * @property label
     * @type String
     * 
     */
    public String getLabel() {
        return getPolymerElement().getLabel();
    }
    /**
     * <p>The label for this element.</p>
     *
     * JavaScript Info:
     * @property label
     * @type String
     * 
     */
    public void setLabel(String value) {
        getPolymerElement().setLabel(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property inputmode
     * @type String
     * 
     */
    public String getInputmode() {
        return getPolymerElement().getInputmode();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property inputmode
     * @type String
     * 
     */
    public void setInputmode(String value) {
        getPolymerElement().setInputmode(value);
    }

    /**
     * <p>The error message to display when the input is invalid.</p>
     *
     * JavaScript Info:
     * @property errorMessage
     * @type String
     * 
     */
    public String getErrorMessage() {
        return getPolymerElement().getErrorMessage();
    }
    /**
     * <p>The error message to display when the input is invalid.</p>
     *
     * JavaScript Info:
     * @property errorMessage
     * @type String
     * 
     */
    public void setErrorMessage(String value) {
        getPolymerElement().setErrorMessage(value);
    }

    /**
     * 
     *
     * JavaScript Info:
     * @property name
     * @type String
     * 
     */
    public String getName() {
        return getPolymerElement().getName();
    }
    /**
     * 
     *
     * JavaScript Info:
     * @property name
     * @type String
     * 
     */
    public void setName(String value) {
        getPolymerElement().setName(value);
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

    // Needed in UIBinder
    /**
     * 
     *
     * JavaScript Info:
     * @attribute size
     * 
     */
    public void setSize(String value) {
        Polymer.property(this, "size", value);
        // getPolymerElement().setAttribute("size", value);
    }

    // Needed in UIBinder
    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @attribute items
     * 
     */
    public void setItems(String value) {
        Polymer.property(this, "items", value);
        // getPolymerElement().setAttribute("items", value);
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
