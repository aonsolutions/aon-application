package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Objects;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;

public class AppController implements Serializable {

	public final static String CONTROLLER_NAME = "app";
	public final static String SUITE_APP_ID = "suiteApp";
	public final static String STANDALONE_APP_ID = "standAloneApp";
	
	
	private String viewId  = null;
	private String action  = null;
	private String actionListener  = null;

	
	public String getViewId() {
		return viewId;
	}
	
	public String getAction() {
		return action;
	}
	
	public String getActionListener() {
		return actionListener;
	}
	
	public boolean isStandAlone() {
		try {
		return isStandAloneApp(FacesContext.getCurrentInstance().getViewRoot());
		} catch ( Exception e ) {
			return false;
		}
	}
	
	public String getSuiteAppId(){
		return SUITE_APP_ID;
	}

	public String getStandAloneAppId(){
		return STANDALONE_APP_ID;
	}
	
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public void setActionListener(String actionListener) {
		this.actionListener = actionListener;
	}
	
	public ValueExpression getActionListenerMethod() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();
		ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
		return expressionFactory.createValueExpression(elContext,"#{" + actionListener + "}",Object.class);
		
	}
	
	private boolean  isStandAloneApp(UIComponent uiComponent) throws Exception {
		if ( Objects.equals(uiComponent.getId(), STANDALONE_APP_ID))
			return true;
		
		if ( Objects.equals(uiComponent.getId(), SUITE_APP_ID))
			throw new Exception(SUITE_APP_ID);
			
		
		for (UIComponent child : uiComponent.getChildren() ) {
			if ( isStandAloneApp(child)) { 
				return true;
			}
		}
		
		return false;
	}
}
