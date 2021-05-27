/*
 * This code was generated with Vaadin Web Component GWT API Generator, 
 * from aon-combo-box project by unknown author
 * that is licensed with unknown license.
 */
package net.aonsolutions.polymer.aon;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.vaadin.polymer.elemental.HTMLElement;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * 
 */
@JsType(isNative=true)
public interface AonComboBoxLightElement extends HTMLElement {

    @JsOverlay public static final String TAG = "aon-combo-box-light";
    @JsOverlay public static final String SRC = "aon-combo-box/doc-imports.html";


    /**
     * <p>If <code>true</code>, the user can input a value that is not present in the items list.<br><code>value</code> property will be set to the input value in this case.<br>Also, when <code>value</code> is set programmatically, the input value will be set<br>to reflect that value.</p>
     *
     * JavaScript Info:
     * @property allowCustomValue
     * @type Boolean
     * 
     */
    @JsProperty boolean getAllowCustomValue();
    /**
     * <p>If <code>true</code>, the user can input a value that is not present in the items list.<br><code>value</code> property will be set to the input value in this case.<br>Also, when <code>value</code> is set programmatically, the input value will be set<br>to reflect that value.</p>
     *
     * JavaScript Info:
     * @property allowCustomValue
     * @type Boolean
     * 
     */
    @JsProperty void setAllowCustomValue(boolean value);

    /**
     * <p>Returns a reference to the input element.</p>
     *
     * JavaScript Info:
     * @property inputElement
     * @type HTMLElement
     * 
     */
    @JsProperty JavaScriptObject getInputElement();
    /**
     * <p>Returns a reference to the input element.</p>
     *
     * JavaScript Info:
     * @property inputElement
     * @type HTMLElement
     * 
     */
    @JsProperty void setInputElement(JavaScriptObject value);

    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @property items
     * @type Array
     * 
     */
    @JsProperty JsArray getItems();
    /**
     * <p>An array of values to be displayed as options in the dropdown. The<br>options can be of either <code>String</code> or <code>Object</code> type.</p>
     *
     * JavaScript Info:
     * @property items
     * @type Array
     * 
     */
    @JsProperty void setItems(JsArray value);

    /**
     * <p>Number of pixels used as the vertical offset in positioning of<br>the dropdown.</p>
     *
     * JavaScript Info:
     * @property overlayVerticalOffset
     * @type Number
     * 
     */
    @JsProperty double getOverlayVerticalOffset();
    /**
     * <p>Number of pixels used as the vertical offset in positioning of<br>the dropdown.</p>
     *
     * JavaScript Info:
     * @property overlayVerticalOffset
     * @type Number
     * 
     */
    @JsProperty void setOverlayVerticalOffset(double value);

    /**
     * <p>The selected item from the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property selectedItem
     * @type Object
     * @behavior AonComboBoxLight
     */
    @JsProperty JavaScriptObject getSelectedItem();
    /**
     * <p>The selected item from the <code>items</code> array.</p>
     *
     * JavaScript Info:
     * @property selectedItem
     * @type Object
     * @behavior AonComboBoxLight
     */
    @JsProperty void setSelectedItem(JavaScriptObject value);

    /**
     * <p>True if the dropdown is open, false otherwise.</p>
     *
     * JavaScript Info:
     * @property opened
     * @type Boolean
     * 
     */
    @JsProperty boolean getOpened();
    /**
     * <p>True if the dropdown is open, false otherwise.</p>
     *
     * JavaScript Info:
     * @property opened
     * @type Boolean
     * 
     */
    @JsProperty void setOpened(boolean value);

    /**
     * <p>A read-only property indicating whether this combo box has a value<br>selected or not. It can be used for example in styling of the component.</p>
     *
     * JavaScript Info:
     * @property hasValue
     * @type Boolean
     * 
     */
    @JsProperty boolean getHasValue();
    /**
     * <p>A read-only property indicating whether this combo box has a value<br>selected or not. It can be used for example in styling of the component.</p>
     *
     * JavaScript Info:
     * @property hasValue
     * @type Boolean
     * 
     */
    @JsProperty void setHasValue(boolean value);

    /**
     * <p>When present, it specifies that the element field is read-only.</p>
     *
     * JavaScript Info:
     * @property readonly
     * @type Boolean
     * 
     */
    @JsProperty boolean getReadonly();
    /**
     * <p>When present, it specifies that the element field is read-only.</p>
     *
     * JavaScript Info:
     * @property readonly
     * @type Boolean
     * 
     */
    @JsProperty void setReadonly(boolean value);

    /**
     * <p>Set to true to disable this element.</p>
     *
     * JavaScript Info:
     * @property disabled
     * @type Boolean
     * 
     */
    @JsProperty boolean getDisabled();
    /**
     * <p>Set to true to disable this element.</p>
     *
     * JavaScript Info:
     * @property disabled
     * @type Boolean
     * 
     */
    @JsProperty void setDisabled(boolean value);

    /**
     * 
     *
     * JavaScript Info:
     * @property filterEnable
     * @type Boolean
     * @behavior AonComboBoxLight
     */
    @JsProperty boolean getFilterEnable();
    /**
     * 
     *
     * JavaScript Info:
     * @property filterEnable
     * @type Boolean
     * @behavior AonComboBoxLight
     */
    @JsProperty void setFilterEnable(boolean value);

    /**
     * <p>Path for the value of the item. If <code>items</code> is an array of objects, the<br><code>itemValuePath:</code> is used to fetch the string value for the selected<br>item.</p>
     * <p>The item value is used in the <code>value</code> property of the combo box,<br>to provide the form value.</p>
     *
     * JavaScript Info:
     * @property itemValuePath
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty String getItemValuePath();
    /**
     * <p>Path for the value of the item. If <code>items</code> is an array of objects, the<br><code>itemValuePath:</code> is used to fetch the string value for the selected<br>item.</p>
     * <p>The item value is used in the <code>value</code> property of the combo box,<br>to provide the form value.</p>
     *
     * JavaScript Info:
     * @property itemValuePath
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty void setItemValuePath(String value);

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
    @JsProperty String getValue();
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
    @JsProperty void setValue(String value);

    /**
     * 
     *
     * JavaScript Info:
     * @property inputElementValue
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty String getInputElementValue();
    /**
     * 
     *
     * JavaScript Info:
     * @property inputElementValue
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty void setInputElementValue(String value);

    /**
     * <p>Name of the two-way data-bindable property representing the<br>value of the custom input field.</p>
     *
     * JavaScript Info:
     * @property attrForValue
     * @type String
     * 
     */
    @JsProperty String getAttrForValue();
    /**
     * <p>Name of the two-way data-bindable property representing the<br>value of the custom input field.</p>
     *
     * JavaScript Info:
     * @property attrForValue
     * @type String
     * 
     */
    @JsProperty void setAttrForValue(String value);

    /**
     * <p>Path for label of the item. If <code>items</code> is an array of objects, the<br><code>itemLabelPath</code> is used to fetch the displayed string label for each<br>item.</p>
     * <p>The item label is also used for matching items when processing user<br>input, i.e., for filtering and selecting items.</p>
     *
     * JavaScript Info:
     * @property itemLabelPath
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty String getItemLabelPath();
    /**
     * <p>Path for label of the item. If <code>items</code> is an array of objects, the<br><code>itemLabelPath</code> is used to fetch the displayed string label for each<br>item.</p>
     * <p>The item label is also used for matching items when processing user<br>input, i.e., for filtering and selecting items.</p>
     *
     * JavaScript Info:
     * @property itemLabelPath
     * @type String
     * @behavior AonComboBoxLight
     */
    @JsProperty void setItemLabelPath(String value);


    /**
     * <p>Closes the dropdown list.</p>
     *
     * JavaScript Info:
     * @method close
     * @behavior AonComboBoxLight
     * 
     */
    void close();

    /**
     * <p>Reverts back to original value.</p>
     *
     * JavaScript Info:
     * @method cancel
     * 
     * 
     */
    void cancel();

    /**
     * <p>Opens the dropdown list.</p>
     *
     * JavaScript Info:
     * @method open
     * 
     * 
     */
    void open();

}
