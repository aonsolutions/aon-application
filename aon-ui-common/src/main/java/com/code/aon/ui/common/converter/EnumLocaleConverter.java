package com.code.aon.ui.common.converter;

import java.util.Collection;
import java.util.Locale;

import jakarta.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.model.SelectItem;

import com.code.aon.common.enumeration.IResourceable;

/**
 * 
 * @author Consulting & Development. Aimar Tellitu - 11-ene-2005
 * @since 1.0
 * 
 */

public class EnumLocaleConverter implements Converter {

	/**
	 * Devuelve el tipo de medio correspondiente.
	 * 
	 * @param name
	 * @param locale
	 * @return El tipo de medio.
	 */
	private Object get(Class<IResourceable> enumType, String name, Locale locale) {
		IResourceable[] enumConstants = enumType.getEnumConstants();
		for (IResourceable element : enumConstants) {
			if (element.getName(locale).equals(name)) {
				return element;
			}
		}
		return null;
	}

	/**
	 * Return Enumeration Class
	 * 
	 * @param ctx
	 * @param comp
	 * @return Class
	 */
	@SuppressWarnings("rawtypes")
	protected Class getEnumClass(FacesContext ctx, UIComponent comp) {
		ValueExpression vb = comp.getValueExpression("value");
		Class enumType = vb == null ? null : vb.getType(ctx.getELContext());
		if (enumType == null || !enumType.isEnum()) {
			for (Object child : comp.getChildren()) {
				if (child instanceof UIComponent) {
					UIComponent c = (UIComponent) child;
					vb = c.getValueExpression("value");
					Object val = vb == null ? null : vb.getValue(ctx.getELContext());
					if (val != null) {
						Class t = val.getClass();
						if (t.isArray() && t.getComponentType().isEnum()) {
							return t;
						} else if (val instanceof Collection) {
							Object item = ((Collection) val).iterator().next();
							if (item instanceof SelectItem) {
								SelectItem si = (SelectItem) item;
								return si.getValue().getClass();
							} 
							t = item.getClass();
							if (t.isArray() && t.getComponentType().isEnum()) {
								return t;
							}
						}
					}
				}
			}
		} else {
			return enumType;
		}
		throw new ConverterException(
				"Unable to find selectItems with enum values.");
	}

	@SuppressWarnings("unchecked")
	public Object getAsObject(FacesContext ctx, UIComponent c, String text)
			throws ConverterException {
		if (text == null) {
			return text;
		}
		Class enumType = getEnumClass(ctx, c);
		if (enumType.isArray()) {
			enumType = enumType.getComponentType();
		}
		Object object = get(enumType, text, ctx.getViewRoot().getLocale());
		return object;
	}

	public String getAsString(FacesContext ctx, UIComponent c, Object value)
			throws ConverterException {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return (String) value;
		}
		IResourceable enumLocalized = (IResourceable) value;
		return enumLocalized.getName(ctx.getViewRoot().getLocale());
	}

}
