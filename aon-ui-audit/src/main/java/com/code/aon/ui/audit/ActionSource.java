package com.code.aon.ui.audit;

import java.io.Serializable;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.ui.util.AonUtil;

public class ActionSource implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String method;
	
	private String value;

	public ActionSource(String method, String value) {
		this.method = method;
		this.value = value;
	}
	
	public ActionSource(String method) {
		this.method = method;
	}

	public void execute() {
		if ( value != null ) {
			setProperty();
		} else {
			executeMethod();
		}
	}

	private void setProperty() {
		Object object = AonUtil.getValue(value);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx, method, Object.class);
		ve.setValue(elctx, object);
	}
	
	private void executeMethod() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ActionEvent event = new ActionEvent(ctx.getViewRoot());
		AonUtil.actionListener(this.method, event);
	}
	
}
