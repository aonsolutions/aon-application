package com.code.aon.ui.common.converter;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;

public class DateConverter implements Converter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DateConverter.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(AonUtil.getMessage(DATE_PATTERN));

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String result = null;
		if (value != null) {
			result = DATE_FORMAT.format(value);
		}
		return result;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Object result = null;
		if (! StringUtils.isEmpty(value)) {
			try {
				result = DATE_FORMAT.parse(value);
			} catch (ParseException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return result;
	}	

}
