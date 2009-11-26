package com.code.aon.faces.component.richfaces.lookup;

import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

public interface ILookupComponent {

	ValueBinding getLookup();

	ValueBinding getProperty();
	
	String getLookupProperty();
	
	MethodBinding getLookupChangeListener();
	
}
