package com.code.aon.faces.component.richfaces.lookup;

import javax.el.MethodExpression;
import javax.el.ValueExpression;

public interface ILookupComponent {

	ValueExpression getLookup();

	ValueExpression getProperty();
	
	String getLookupProperty();
	
	MethodExpression getLookupChangeListener();
	
}
