package com.code.aon.ui.geozone.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.util.AonUtil;

public class GeoZoneConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext ctx, UIComponent comp, String value) {

		if (StringUtils.isEmpty(value)) {
			return null;
		}
		Integer id;
		try {
			id = new Integer(value);	
		} catch (NumberFormatException e) {
			String msg = AonUtil.addErrorMessageFromBundle(  ICommonConstants.NUMERIC_ERROR );
			throw new ConverterException( msg );
		}
		
		try {
			IManagerBean  bean = BeanManager.getManagerBean(GeoZone.class);
			GeoZone p = (GeoZone) bean.get(id);
			if (p==null) {
				p = new GeoZone();
				p.setId(id);
				p.setName(" -------------------------- ");
			}
			return p;
		} catch (ManagerBeanException e) {
			String msg = AonUtil.addErrorMessageFromBundle(  ICommonConstants.CONVERSION_ERROR );
			throw new ConverterException(msg,e);
		}
	}

	@Override
	public String getAsString(FacesContext ctx, UIComponent c, Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof GeoZone) {
			GeoZone a = (GeoZone) value;
			if (a.getId() == null) {
				return null;
			}
			return a.getId().toString();
		}
		String msg = AonUtil.addErrorMessageFromBundle(  ICommonConstants.CONVERSION_ERROR );
		throw new ConverterException(msg);
	}
	
}
