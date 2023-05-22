package com.code.aon.faces.component.richfaces.lookup;

import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

import com.code.aon.ui.form.event.IControllerListener;

public interface ILookupComponent {

	void setProperty( ValueExpression ve );
	
	ValueExpression getProperty();
	
	String getLookupProperty();
	
	MethodExpression getLookupChangeListener();
	
	void setLookupChangeListener( MethodExpression me );
	
	IControllerListener getControllerListener();
	
}
