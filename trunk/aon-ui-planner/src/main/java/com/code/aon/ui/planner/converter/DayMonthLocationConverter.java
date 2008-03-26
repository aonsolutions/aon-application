package com.code.aon.ui.planner.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.planner.enumeration.DayMonthLocation;
import com.code.aon.ui.planner.util.PlannerUtil;

/**
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 24-nov-2005
 * @since 1.0
 *
 */

public class DayMonthLocationConverter implements Converter {

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
     */
    public Object getAsObject(FacesContext ctx, UIComponent c, String text)
            throws ConverterException {
    	DayMonthLocation result = null;
		try {
		    if (text == null || text.trim().equals(PlannerUtil.EMPTY_STRING)) {
		        return text;
		    }
		    int i = Integer.parseInt(text);
		    result = DayMonthLocation.get((byte) i);
		} catch (NumberFormatException e) {
			result = DayMonthLocation.get(text, ctx.getViewRoot().getLocale());
		}
		return result;
    }

    /* (non-Javadoc)
     * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
     */
    public String getAsString(FacesContext ctx, UIComponent c, Object value)
            throws ConverterException {
		if (value == null) {
		    return null;
		}
		if (! (value instanceof DayMonthLocation) ) {
			return value.toString();
		}
		String result = ((DayMonthLocation)value).getName(ctx.getViewRoot().getLocale());
		return result;
    }

}
