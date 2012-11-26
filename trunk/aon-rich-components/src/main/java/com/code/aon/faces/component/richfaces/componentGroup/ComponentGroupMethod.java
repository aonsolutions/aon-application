package com.code.aon.faces.component.richfaces.componentGroup;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.facelets.FaceletContext;

public class ComponentGroupMethod {
	
	private String[] componentFamilies;
	
	private MethodExpression method;

	public ComponentGroupMethod(String componentFamily, MethodExpression method ) {
		this.componentFamilies = StringUtils.split(componentFamily, ",");
		this.method = method;
	}

	public boolean isAppicable( UIComponent component ) {
		return ArrayUtils.contains(this.componentFamilies, component.getFamily());
	}
	
	public void apply( FaceletContext ctx, UIComponent component, UIComponent parent ) {
		this.method.invoke( ctx, new Object[] {component, parent} );
	}
	
}