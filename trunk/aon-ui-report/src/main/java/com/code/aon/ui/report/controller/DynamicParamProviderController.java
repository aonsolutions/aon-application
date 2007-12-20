package com.code.aon.ui.report.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.report.IReportDynamicParamsProvider;

public class DynamicParamProviderController implements
		IReportDynamicParamsProvider {

	private String controllerExpression;

	private String beanPropertyName;

	private List<String> dynamicParams;
	
	private static Logger LOGGER = Logger.getLogger(DynamicParamProviderController.class.getName());

	public String getControllerExpression() {
		return controllerExpression;
	}

	public void setControllerExpression(String controllerExpression) {
		this.controllerExpression = controllerExpression;
	}

	public Map<String, Object> getDynamicParamsMap() {
		Object controller = getController();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Object to = PropertyUtils.getProperty(
					controller, getBeanPropertyName());
			LOGGER.finest( to.getClass().getName());
			LOGGER.finest(dynamicParams.toString());
			for (Object field : dynamicParams) {
				String fieldName = (String) field;  
				Object fieldValue = PropertyUtils.getProperty(to,
						fieldName);
				LOGGER.finest( "DynamicParam " + fieldName + " " + " Value: " + fieldValue.toString());
				map.put(fieldName, fieldValue);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LOGGER.finest( "DynamicParams size = " + map.size() );

		return map;
	}

	private Object getController() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Application app = ctx.getApplication();
		ValueBinding vb = app.createValueBinding("#{" + getControllerExpression() + "}");
		return vb.getValue(ctx);
	}

	public String getBeanPropertyName() {
		return beanPropertyName;
	}

	public void setBeanPropertyName(String beanPropertyName) {
		this.beanPropertyName = beanPropertyName;
	}

	public List<String> getDynamicParams() {
		return dynamicParams;
	}

	public void setDynamicParams(List<String> dynamicParams) {
		System.out.println(dynamicParams);
		this.dynamicParams = dynamicParams;
	}
}
