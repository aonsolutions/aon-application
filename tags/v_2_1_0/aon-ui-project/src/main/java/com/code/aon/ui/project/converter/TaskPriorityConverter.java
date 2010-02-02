package com.code.aon.ui.project.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.ui.converter.EnumLocaleConverter;

/**
 * Converter used by the <code>TaskPriority</code> class.
 */
public class TaskPriorityConverter extends EnumLocaleConverter {

	/**
     * Gets the enum class.
     * 
     * @param c the Component
     * @param ctx the Context
     * 
     * @return the enum class
     */
	@SuppressWarnings("unchecked")
    protected Class getEnumClass( FacesContext ctx, UIComponent c ) {
    	return Priority.class;
    }
}
