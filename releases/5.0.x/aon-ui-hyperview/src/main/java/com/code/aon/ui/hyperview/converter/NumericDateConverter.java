package com.code.aon.ui.hyperview.converter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.code.aon.hyperview.renderer.HyperViewRendererUtils;

/**
 * @author Consulting & Development. ecastellano - 13-jun-2006
 * 
 */
public class NumericDateConverter implements Converter {

	private DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

	private HyperViewRendererUtils rendererUtils;
	
	public Object getAsObject(FacesContext ctx, UIComponent c, String text)
			throws ConverterException {
		return null;
	}

	public String getAsString(FacesContext ctx, UIComponent c, Object value)
			throws ConverterException {
		if (value == null) {
			return null;
		}

		Date date = null;
		if (value instanceof Date) {
			date = (Date) value;
		} else if (value instanceof Number) {
			Number number = (Number) value;
			if ( number.intValue() == 0 ) {
				return null;
			}
			date = getDate( number, ctx );
		} else {
			return null;
		}
		if (date != null) {
			return dateFormatter.format(date);
		}
		return null;
	}

	private Date getDate(Number number,FacesContext ctx) {
		try {
			Date date = getHyperViewRendererUtils(ctx).getDate( number );
			return date; 
		} catch (Exception e) {
			throw new ConverterException("Unknown Date... '" + number + "'. " + e.getMessage());
		}
		
		
	}
	
	public HyperViewRendererUtils getHyperViewRendererUtils(FacesContext ctx) {
		if (rendererUtils == null) {
			Application app = ctx.getApplication(); 
			String baseName = app.getMessageBundle();
			Locale locale = ctx.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
			rendererUtils = new HyperViewRendererUtils(bundle);
		}
		return rendererUtils;
	}

}
