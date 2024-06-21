package com.code.aon.aio.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import antlr.actions.cpp.ActionLexer;
import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;

public class AppController implements Serializable {

	public final static String CONTROLLER_NAME = "app";
	
	private String viewId  = null;
	private String action  = null;
	private String actionListener  = null;
	private boolean standAlone = false;

	
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
		return standAlone;
	}
	
	public void setStandAlone(boolean inside) {
		this.standAlone = inside;
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
	
	
}
