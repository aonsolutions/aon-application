/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.code.aon.faces.component.tomahawk;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.faces.FacesException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIOutput;
import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.component.UIViewRoot;
import javax.faces.component.ValueHolder;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.el.PropertyNotFoundException;
import javax.faces.el.ValueBinding;
import javax.faces.event.PhaseId;
import javax.faces.model.SelectItem;

/**
 * @author Manfred Geiler (latest modification by $Author$)
 * @version $Revision$ $Date$
 */
public final class RendererUtils {

    private RendererUtils() {
        //nope
    }

    public static final String SELECT_ITEM_LIST_ATTR = RendererUtils.class.getName() + ".LIST";
    public static final String EMPTY_STRING = "";
    public static final Object NOTHING = new Serializable() {
    };

    public static final String ACTION_FOR_LIST = "org.apache.myfaces.ActionForList";
    public static final String ACTION_FOR_PHASE_LIST = "org.apache.myfaces.ActionForPhaseList";

    public static String getPathToComponent(UIComponent component) {
        StringBuffer buf = new StringBuffer();

        if (component == null) {
            buf.append("{Component-Path : ");
            buf.append("[null]}");
            return buf.toString();
        }

        getPathToComponent(component, buf);

        buf.insert(0, "{Component-Path : ");
        buf.append("}");

        return buf.toString();
    }

    private static void getPathToComponent(UIComponent component, StringBuffer buf) {
        if (component == null)
            return;

        StringBuffer intBuf = new StringBuffer();

        intBuf.append("[Class: ");
        intBuf.append(component.getClass().getName());
        if (component instanceof UIViewRoot) {
            intBuf.append(",ViewId: ");
            intBuf.append(((UIViewRoot) component).getViewId());
        }
        else {
            intBuf.append(",Id: ");
            intBuf.append(component.getId());
        }
        intBuf.append("]");

        buf.insert(0, intBuf.toString());

        getPathToComponent(component.getParent(), buf);
    }

    public static String getConcatenatedId(FacesContext context, UIComponent container,
                                           String clientId) {
        UIComponent child = container.findComponent(clientId);

        if (child == null)
            return clientId;

        return getConcatenatedId(context, child);
    }

    public static String getConcatenatedId(FacesContext context, UIComponent component) {
        if (context == null) throw new NullPointerException("context");

        StringBuffer idBuf = new StringBuffer();

        idBuf.append(component.getId());

        UIComponent parent;

        while ((parent = component.getParent()) != null) {
            if (parent instanceof NamingContainer) {
                idBuf.insert(0, NamingContainer.SEPARATOR_CHAR);
                idBuf.insert(0, parent.getId());
            }
        }

        return idBuf.toString();
    }

    public static Boolean getBooleanValue(UIComponent component) {
        Object value = getObjectValue(component);
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        }
        else {
            throw new IllegalArgumentException("Expected submitted value of type Boolean for Component : " +
                getPathToComponent(component));
        }
    }

    public static Date getDateValue(UIComponent component) {
        Object value = getObjectValue(component);
        if (value == null || value instanceof Date) {
            return (Date) value;
        }
        else {
            throw new IllegalArgumentException("Expected submitted value of type Date for component : "
                + getPathToComponent(component));
        }
    }

    public static Object getObjectValue(UIComponent component) {
        if (!(component instanceof ValueHolder)) {
            throw new IllegalArgumentException("Component : " +
                getPathToComponent(component) + "is not a ValueHolder");
        }

        if (component instanceof EditableValueHolder) {
            Object value = ((EditableValueHolder) component).getSubmittedValue();
            if (value != null && !NOTHING.equals(value)) {
                return value;
            }
        }

        return ((ValueHolder) component).getValue();
    }

    public static String getStringValue(FacesContext context, ValueBinding vb)
    {
        Object value = vb.getValue(context);
        if (value != null)
        {
            return value.toString();
        }
        return null;
    }

    public static String getStringValue(FacesContext facesContext,
                                        UIComponent component) {
        try {
            if (!(component instanceof ValueHolder)) {
                throw new IllegalArgumentException("Component : " + getPathToComponent(component) + "is not a ValueHolder");
            }

            if (component instanceof EditableValueHolder) {
                Object submittedValue = ((EditableValueHolder) component).getSubmittedValue();
                if (submittedValue != null) {
                    if (submittedValue instanceof String) {
                        return (String) submittedValue;
                    }
                    else {
                        throw new IllegalArgumentException("Expected submitted value of type String for component : "
                            + getPathToComponent(component));
                    }
                }
            }

            Object value;

            try
            {
                value = ((ValueHolder) component).getValue();
            }
            catch(Exception ex)
            {
                throw new FacesException("Could not retrieve value of component with path : "+
                        getPathToComponent(component),ex);
            }

            Converter converter = ((ValueHolder) component).getConverter();
            if (converter == null && value != null) {
                if (value instanceof String) {
                    return (String) value;
                }

                try {
                    converter = facesContext.getApplication().createConverter(value.getClass());
                }
                catch (FacesException e) {
                    e.printStackTrace();
                    // converter stays null
                }
            }

            if (converter == null) {
                if (value == null) {
                    return "";
                }
                else {
                    return value.toString();
                }
            }
            else {
                return converter.getAsString(facesContext, component, value);
            }
        }
        catch (PropertyNotFoundException ex) {
            ex.printStackTrace();

            throw ex;
        }
    }

    /**
     * See JSF Spec. 8.5 Table 8-1
     *
     * @param value
     * @return boolean
     */
    public static boolean isDefaultAttributeValue(Object value) {
        if (value == null) {
            return true;
        }
        else if (value instanceof Boolean) {
            return !((Boolean) value).booleanValue();
        }
        else if (value instanceof Number) {
            if (value instanceof Integer) {
                return ((Number) value).intValue() == Integer.MIN_VALUE;
            }
            else if (value instanceof Double) {
                return ((Number) value).doubleValue() == Double.MIN_VALUE;
            }
            else if (value instanceof Long) {
                return ((Number) value).longValue() == Long.MIN_VALUE;
            }
            else if (value instanceof Byte) {
                return ((Number) value).byteValue() == Byte.MIN_VALUE;
            }
            else if (value instanceof Float) {
                return ((Number) value).floatValue() == Float.MIN_VALUE;
            }
            else if (value instanceof Short) {
                return ((Number) value).shortValue() == Short.MIN_VALUE;
            }
        }
        return false;
    }

    /**
     * Find the proper Converter for the given UIOutput component.
     *
     * @return the Converter or null if no Converter specified or needed
     * @throws FacesException if the Converter could not be created
     */
    public static Converter findUIOutputConverter(FacesContext facesContext,
                                                  UIOutput component)
        throws FacesException {
        return _SharedRendererUtils.findUIOutputConverter(facesContext, component);
    }


    /**
     * Find proper Converter for the entries in the associated List or Array of
     * the given UISelectMany as specified in API Doc of UISelectMany.
     *
     * @return the Converter or null if no Converter specified or needed
     * @throws FacesException if the Converter could not be created
     */
    public static Converter findUISelectManyConverter(FacesContext facesContext,
                                                      UISelectMany component) {
        Converter converter = component.getConverter();
        if (converter != null) return converter;

        //Try to find out by value binding
        ValueBinding vb = component.getValueBinding("value");
        if (vb == null) return null;

        Class valueType = vb.getType(facesContext);
        if (valueType == null) return null;

        if (List.class.isAssignableFrom(valueType)) {
            //According to API Doc of UISelectMany the assumed entry type for a List is String
            //--> so basically no converter needed

            // However, if the List contains something other than Strings, we can attempt
            // to find a suitable converter. In JDK 1.4, we can try to find out what the List
            // contains by looking at the SelectItem value of the first item. With generics in
            // JDK 1.5, it would be much easier to determine the type.

            List selectItems = RendererUtils.internalGetSelectItemList(component);

            if (selectItems != null && selectItems.size() > 0) {
                SelectItem selectItem = (SelectItem) selectItems.get(0);
                Class listComponentType = selectItem.getValue().getClass();

                if (!(String.class.equals(listComponentType))) {
                    try {
                        return facesContext.getApplication().createConverter(listComponentType);
                    }
                    catch (FacesException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }

            return null;
        }

        if (!valueType.isArray()) {
            throw new IllegalArgumentException("ValueBinding for UISelectMany : " + getPathToComponent(component) + " must be of type List or Array");
        }

        Class arrayComponentType = valueType.getComponentType();
        if (String.class.equals(arrayComponentType)) return null;    //No converter needed for String type
        if (Object.class.equals(arrayComponentType)) return null;    //There is no converter for Object class

        try {
            return facesContext.getApplication().createConverter(arrayComponentType);
        }
        catch (FacesException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void checkParamValidity(FacesContext facesContext, UIComponent uiComponent, Class compClass) {
        if (facesContext == null)
            throw new NullPointerException("facesContext may not be null");
        if (uiComponent == null)
            throw new NullPointerException("uiComponent may not be null");

        //if (compClass != null && !(compClass.isAssignableFrom(uiComponent.getClass())))
        // why isAssignableFrom with additional getClass method call if isInstance does the same?
        if (compClass != null && !(compClass.isInstance(uiComponent))) {
            throw new IllegalArgumentException("uiComponent : " + getPathToComponent(uiComponent) +
                " is not instance of " + compClass.getName() + " as it should be");
        }
    }


    public static void renderChildren(FacesContext facesContext, UIComponent component)
        throws IOException {
        if (component.getChildCount() > 0) {
            for (Iterator it = component.getChildren().iterator(); it.hasNext();) {
                UIComponent child = (UIComponent) it.next();
                renderChild(facesContext, child);
            }
        }
    }


    public static void renderChild(FacesContext facesContext, UIComponent child)
        throws IOException {
        if (!child.isRendered()) {
            return;
        }

        child.encodeBegin(facesContext);
        if (child.getRendersChildren()) {
            child.encodeChildren(facesContext);
        }
        else {
            renderChildren(facesContext, child);
        }
        child.encodeEnd(facesContext);
    }


    /**
     * @param uiSelectOne
     * @return List of SelectItem Objects
     */
    public static List getSelectItemList(UISelectOne uiSelectOne) {
        return internalGetSelectItemList(uiSelectOne);
    }

    /**
     * @param uiSelectMany
     * @return List of SelectItem Objects
     */
    public static List getSelectItemList(UISelectMany uiSelectMany) {
        return internalGetSelectItemList(uiSelectMany);
    }

    private static List internalGetSelectItemList(UIComponent uiComponent) {
        /* TODO: Shall we cache the list in a component attribute?
        ArrayList list = (ArrayList)uiComponent.getAttributes().get(SELECT_ITEM_LIST_ATTR);
        if (list != null)
        {
            return list;
        }
         */

        List list = new ArrayList();

        for (Iterator iter = new SelectItemsIterator(uiComponent); iter.hasNext();) {
            list.add(iter.next());
        }
        return list;
    }


    /**
     * Convenient utility method that returns the currently submitted values of
     * a UISelectMany component as a Set, of which the contains method can then be
     * easily used to determine if a select item is currently selected.
     * Calling the contains method of this Set with the renderable (String converted) item value
     * as argument returns true if this item is selected.
     *
     * @param uiSelectMany
     * @return Set containing all currently selected values
     */
    public static Set getSubmittedValuesAsSet(FacesContext context, UIComponent component, Converter converter, UISelectMany uiSelectMany) {
        Object submittedValues = uiSelectMany.getSubmittedValue();
        if (submittedValues == null) {
            return null;
        }
        
        if(converter != null) {
            converter = new PassThroughAsStringConverter(converter);
        }

        return internalSubmittedOrSelectedValuesAsSet(context, component, converter, uiSelectMany, submittedValues);
    }


    /**
     * Convenient utility method that returns the currently selected values of
     * a UISelectMany component as a Set, of which the contains method can then be
     * easily used to determine if a value is currently selected.
     * Calling the contains method of this Set with the item value
     * as argument returns true if this item is selected.
     *
     * @param uiSelectMany
     * @return Set containing all currently selected values
     */
    public static Set getSelectedValuesAsSet(FacesContext context, UIComponent component, Converter converter, UISelectMany uiSelectMany) {
        Object selectedValues = uiSelectMany.getValue();

        return internalSubmittedOrSelectedValuesAsSet(context, component, converter, uiSelectMany, selectedValues);
    }


    /**
     * Convenient utility method that returns the currently given value as String,
     * using the given converter.
     * Especially usefull for dealing with primitive types.
     */
    public static String getConvertedStringValue(FacesContext context,
                                                 UIComponent component, Converter converter, Object value) {
        if (converter == null) {
            if (value == null) {
                return "";
            }
            else if (value instanceof String) {
                return (String) value;
            }
            else {
                throw new IllegalArgumentException(
                    "Value is no String (class=" + value.getClass().getName() + ", value=" + value + ") and component "
                        + component.getClientId(context) + "with path: "
                        + getPathToComponent(component)
                        + " does not have a Converter");
            }
        }

        return converter.getAsString(context, component, value);
    }


    /**
     * Convenient utility method that returns the currently given SelectItem value
     * as String, using the given converter.
     * Especially usefull for dealing with primitive types.
     */
    public static String getConvertedStringValue(FacesContext context,
                                                 UIComponent component, Converter converter, SelectItem selectItem) {
        return getConvertedStringValue(context, component, converter, selectItem.getValue());
    }


    private static Set internalSubmittedOrSelectedValuesAsSet(FacesContext context,
                                                              UIComponent component, Converter converter, UISelectMany uiSelectMany,
                                                              Object values) {
        if (values == null || EMPTY_STRING.equals(values)) {
            return Collections.EMPTY_SET;
        }
        else if (values instanceof Object[]) {
            //Object array
            Object[] ar = (Object[]) values;
            if (ar.length == 0) {
                return Collections.EMPTY_SET;
            }

            HashSet set = new HashSet(HashMapUtils.calcCapacity(ar.length));
            for (int i = 0; i < ar.length; i++) {
                set.add(getConvertedStringValue(context, component, converter, ar[i]));
            }
            return set;
        }
        else if (values.getClass().isArray()) {
            //primitive array
            int len = Array.getLength(values);
            HashSet set = new HashSet(HashMapUtils.calcCapacity(len));
            for (int i = 0; i < len; i++) {
                set.add(getConvertedStringValue(context, component, converter, Array.get(values, i)));
            }
            return set;
        }
        else if (values instanceof List) {
            List lst = (List) values;
            if (lst.size() == 0) {
                return Collections.EMPTY_SET;
            }
            else {
                HashSet set = new HashSet(HashMapUtils.calcCapacity(lst.size()));
                for (Iterator i = lst.iterator(); i.hasNext();)
                    set.add(getConvertedStringValue(context, component, converter, i.next()));

                return set;
            }
        }
        else {
            throw new IllegalArgumentException("Value of UISelectMany component with path : " + getPathToComponent(uiSelectMany) + " is not of type Array or List");
        }
    }


    public static Object getConvertedUIOutputValue(FacesContext facesContext,
                                                   UIOutput output,
                                                   Object submittedValue)
        throws ConverterException {
        if (submittedValue != null && !(submittedValue instanceof String)) {
            if (RendererUtils.NOTHING.equals(submittedValue)) {
                return null;
            }
            throw new IllegalArgumentException("Submitted value of type String for component : " +
                getPathToComponent(output) + "expected");
        }

        /* earlier code used to do the following around findUIOutputConverter:
        try {
        }
        catch (FacesException e) {
            throw new ConverterException(e);
        }
        mmarinschek: I believe this is wrong - as it silently swallows exceptions relating to the value-binding of a component.
        for better error-handling, I disabled this code
        */
        Converter converter = findUIOutputConverter(facesContext, output);

        if (converter == null) {
            //No conversion needed
            return submittedValue;
        }
        else {
            //Conversion
            return converter.getAsObject(facesContext, output, (String) submittedValue);
        }
    }


    public static Object getConvertedUISelectManyValue(FacesContext facesContext,
                                                       UISelectMany selectMany,
                                                       Object submittedValue)
        throws ConverterException {
        if (submittedValue == null) {
            return null;
        }
        else {
            if (!(submittedValue instanceof String[])) {
                throw new ConverterException("Submitted value of type String[] for component : "
                    + getPathToComponent(selectMany) + "expected");
            }
        }
        return _SharedRendererUtils.getConvertedUISelectManyValue(facesContext,
                                                                                                      selectMany,
                                                                                                      (String[]) submittedValue);
    }


    public static boolean getBooleanAttribute(UIComponent component,
                                              String attrName,
                                              boolean defaultValue) {
        Boolean b = (Boolean) component.getAttributes().get(attrName);
        return b != null ? b.booleanValue() : defaultValue;
    }

    public static int getIntegerAttribute(UIComponent component,
                                          String attrName,
                                          int defaultValue) {
        Integer i = (Integer) component.getAttributes().get(attrName);
        return i != null ? i.intValue() : defaultValue;
    }

    private static final String TRINIDAD_FORM_COMPONENT_FAMILY = "org.apache.myfaces.trinidad.Form";
    private static final String ADF_FORM_COMPONENT_FAMILY = "oracle.adf.Form";


    /**
     * Find the enclosing form of a component
     * in the view-tree.
     * All Subclasses of <code>UIForm</code> and all known
     * form-families are searched for.
     * Currently those are the Trinidad form family,
     * and the (old) ADF Faces form family.
     * <p/>
     * There might be additional form families
     * which have to be explicitly entered here.
     *
     * @param uiComponent
     * @param facesContext
     * @return FormInfo Information about the form - the form itself and its name.
     */
    public static FormInfo findNestingForm(UIComponent uiComponent, FacesContext facesContext)
    {
        UIComponent parent = uiComponent.getParent();
        while (parent != null && (!ADF_FORM_COMPONENT_FAMILY.equals(parent.getFamily()) &&
            !TRINIDAD_FORM_COMPONENT_FAMILY.equals(parent.getFamily()) &&
            !(parent instanceof UIForm)))
        {
            parent = parent.getParent();
        }

        if (parent != null)
        {
            //link is nested inside a form
            String formName = parent.getClientId(facesContext);
            return new FormInfo(parent, formName);
        }

        return null;
    }

    public static boolean getBooleanValue(String attribute, Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        else if (value instanceof String) {
            return Boolean.valueOf((String) value).booleanValue();
        }
        else if (value != null) {

            return defaultValue;
        }

        return defaultValue;
    }

    public static void copyHtmlInputTextAttributes(HtmlInputText src, HtmlInputText dest) {
        dest.setId(src.getId());
        boolean forceId = getBooleanValue(
            JSFAttr.FORCE_ID_ATTR,
            src.getAttributes().get(JSFAttr.FORCE_ID_ATTR),
            false);
        if (forceId) {
            dest.getAttributes().put(JSFAttr.FORCE_ID_ATTR, Boolean.TRUE);
        }
        dest.setImmediate(src.isImmediate());
        dest.setTransient(src.isTransient());
        dest.setAccesskey(src.getAccesskey());
        dest.setAlt(src.getAlt());
        dest.setConverter(src.getConverter());
        dest.setDir(src.getDir());
        dest.setDisabled(src.isDisabled());
        dest.setLang(src.getLang());
        dest.setLocalValueSet(src.isLocalValueSet());
        dest.setMaxlength(src.getMaxlength());
        dest.setOnblur(src.getOnblur());
        dest.setOnchange(src.getOnchange());
        dest.setOnclick(src.getOnclick());
        dest.setOndblclick(src.getOndblclick());
        dest.setOnfocus(src.getOnfocus());
        dest.setOnkeydown(src.getOnkeydown());
        dest.setOnkeypress(src.getOnkeypress());
        dest.setOnkeyup(src.getOnkeyup());
        dest.setOnmousedown(src.getOnmousedown());
        dest.setOnmousemove(src.getOnmousemove());
        dest.setOnmouseout(src.getOnmouseout());
        dest.setOnmouseover(src.getOnmouseover());
        dest.setOnmouseup(src.getOnmouseup());
        dest.setOnselect(src.getOnselect());
        dest.setReadonly(src.isReadonly());
        dest.setRendered(src.isRendered());
        dest.setRequired(src.isRequired());
        dest.setSize(src.getSize());
        dest.setStyle(src.getStyle());
        dest.setStyleClass(src.getStyleClass());
        dest.setTabindex(src.getTabindex());
        dest.setTitle(src.getTitle());
        dest.setValidator(src.getValidator());
    }

    public static UIComponent findComponent(UIComponent headerComp, Class clazz) {
        if (clazz.isAssignableFrom(headerComp.getClass())) {
            return headerComp;
        }

        List li = headerComp.getChildren();

        for (int i = 0; i < li.size(); i++) {
            UIComponent comp = (UIComponent) li.get(i);

            //recursively iterate through children to find the component
            UIComponent lookupComp = findComponent(comp, clazz);

            if (lookupComp != null)
                return lookupComp;
        }

        return null;
    }

    public static void addOrReplaceChild(UIInput component, UIComponent child) {
        List li = component.getChildren();

        for (int i = 0; i < li.size(); i++) {
            UIComponent oldChild = (UIComponent) li.get(i);

            if (oldChild.getId() != null && oldChild.getId().equals(child.getId())) {
                li.set(i, child);
                return;
            }
        }

        component.getChildren().add(child);
    }

    public static String getClientId(FacesContext facesContext, UIComponent uiComponent, String forAttr) {
        UIComponent forComponent = uiComponent.findComponent(forAttr);
        if (forComponent == null) {
            if (forAttr.length() > 0 && forAttr.charAt(0) == UINamingContainer.SEPARATOR_CHAR) {
                //absolute id path
                return forAttr.substring(1);
            }
            else {
                //relative id path, we assume a component on the same level as the label component
                String labelClientId = uiComponent.getClientId(facesContext);
                int colon = labelClientId.lastIndexOf(UINamingContainer.SEPARATOR_CHAR);
                if (colon == -1) {
                    return forAttr;
                }
                else {
                    return labelClientId.substring(0, colon + 1) + forAttr;
                }
            }
        }
        else {
            return forComponent.getClientId(facesContext);
        }
    }

    public static List convertIdsToClientIds(String actionFor, FacesContext facesContext, UIComponent component) {
        List li = new ArrayList();

        String[] ids = actionFor.split(",");

        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals("none"))
                li.add(ids[i]);
            else
                li.add(RendererUtils.getClientId(facesContext, component, ids[i]));
        }
        return li;
    }

    public static List convertPhasesToPhasesIds(String actionForPhase) {
        List li = new ArrayList();

        if (actionForPhase == null) {
            return li;
        }

        String[] ids = actionForPhase.split(",");

        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals("PROCESS_VALIDATIONS")) {
                li.add(PhaseId.PROCESS_VALIDATIONS);
            }
            else if (ids[i].equals("UPDATE_MODEL_VALUES")) {
                li.add(PhaseId.UPDATE_MODEL_VALUES);
            }
        }
        return li;
    }

    /**
     * Helper method which loads a resource file (such as css) by a given context path and a file name.
     * Useful to provide css files (or js files) inline.
     *
     * @param ctx  <code>FacesContext</code> object to calculate the context path of the web application.
     * @param file name of the resource file (e.g. <code>foo.css</code>).
     * @return the content of the resource file, or <code>null</code> if no such file is available.
     */
    public static String loadResourceFile(FacesContext ctx, String file) {

        ByteArrayOutputStream content = new ByteArrayOutputStream(10240);

		InputStream in = null;
		try {
			in = ctx.getExternalContext().getResourceAsStream(file);
			if (in == null)
			{
				return null;
			}

			byte[] fileBuffer = new byte[10240];
			int read;
			while ((read = in.read(fileBuffer)) > -1)
			{
				content.write(fileBuffer, 0, read);
			}
		}
		catch (FileNotFoundException e) {
			content = null;
		}
		catch (IOException e) {
			content = null;
		}
		finally
		{
			try
			{
				content.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

		return content.toString();
    }

    /**
     * check for partial validation or model update attributes being set
     * and initialize the request-map accordingly.
     * SubForms will work with this information.
     */
    public static void initPartialValidationAndModelUpdate(UIComponent component, FacesContext facesContext) {
        String actionFor = (String) component.getAttributes().get("actionFor");

        if (actionFor != null) {
            List li = convertIdsToClientIds(actionFor, facesContext, component);

            facesContext.getExternalContext().getRequestMap().put(ACTION_FOR_LIST, li);

            String actionForPhase = (String) component.getAttributes().get("actionForPhase");

            if (actionForPhase != null) {
                List phaseList = convertPhasesToPhasesIds(actionForPhase);

                facesContext.getExternalContext().getRequestMap().put(ACTION_FOR_PHASE_LIST, phaseList);
            }
        }
    }

    public static boolean isAdfOrTrinidadForm(UIComponent component) {
        if (component == null)
            return false;
        return ADF_FORM_COMPONENT_FAMILY.equals(component.getFamily()) ||
            TRINIDAD_FORM_COMPONENT_FAMILY.equals(component.getFamily());
    }


    /**
     * Special converter for handling submitted values which don't need to be converted. 
     * 
     * @author mathias (latest modification by $Author$)
     * @version $Revision$ $Date$
     */
    private static class PassThroughAsStringConverter implements Converter
    {
        private final Converter converter;

        public PassThroughAsStringConverter(Converter converter)
        {
            this.converter = converter;
        }

        public Object getAsObject(FacesContext context, UIComponent component,
                String value) throws ConverterException
        {
            return converter.getAsObject(context, component, value);
        }

        public String getAsString(FacesContext context, UIComponent component,
                Object value) throws ConverterException
        {
            return (String)value;
        }

    }
}
