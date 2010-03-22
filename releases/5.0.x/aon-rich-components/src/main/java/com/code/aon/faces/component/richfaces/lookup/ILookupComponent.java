package com.code.aon.faces.component.richfaces.lookup;

import javax.el.MethodExpression;
import javax.el.ValueExpression;

import com.code.aon.ui.form.event.IControllerListener;

public interface ILookupComponent {

	ValueExpression getProperty();
	
	String getLookupProperty();
	
	MethodExpression getLookupChangeListener();
	
	IControllerListener getControllerListener();
	
}
