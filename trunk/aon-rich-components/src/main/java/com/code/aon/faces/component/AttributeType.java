package com.code.aon.faces.component;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.myfaces.UIComponentTagUtils;

public enum AttributeType {

	STRING,
	
	BOOLEAN,
	
	INTEGER,
	
	LONG;
	
	public void setValue( FacesContext ctx, UIComponent component, String attribute, String value ) {
		switch (this) {
			case STRING:
				UIComponentTagUtils.setStringProperty( ctx, component, attribute, value );
				break;
			case BOOLEAN:
				UIComponentTagUtils.setBooleanProperty( ctx, component, attribute, value );
				break;
			case INTEGER:
				UIComponentTagUtils.setIntegerProperty( ctx, component, attribute, value );
				break;
			case LONG:
				UIComponentTagUtils.setLongProperty( ctx, component, attribute, value );
				break;
		}
	}
	
}
