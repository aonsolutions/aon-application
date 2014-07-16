package com.code.aon.ui.common.converter;

import static com.code.aon.ui.common.ICommonMessages.DATE_PATTERN;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ui.util.AonUtil;

public class DateConverter implements Converter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DateConverter.class);

	private String pattern = AonUtil.getMessage(DATE_PATTERN);

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value != null) {
			return new SimpleDateFormat(pattern).format(value);	
		}
		return null;
	}
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value != null) {
			try {
				return DateUtils.parseDate(value, new String[]{pattern});
			} catch (ParseException ex) {
				LOGGER.error(ex.getMessage(), ex);
			}
		}
		return null;
	}	

}
