package com.code.aon.faces.component.icefaces.lookup;

import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;

public interface ILookupComponent {

	ValueBinding getLookup();

	ValueBinding getProperty();
	
	MethodBinding getValueChangeListener();
	
}
