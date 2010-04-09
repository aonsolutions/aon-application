package com.code.aon.faces.component.richfaces.componentGroup;

import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class ComponentGroup {
	
	public static final String CURRENT_COMPONENT_GROUP = "com.code.aon.faces.ComponentGroup.current";

	private String[] componentFamilies;
	
	private MethodExpression method;

	public ComponentGroup(String componentFamily, MethodExpression method ) {
		this.componentFamilies = StringUtils.split(componentFamily, ",");
		this.method = method;
	}

	public boolean isAppicable( UIComponent component ) {
		return ArrayUtils.contains(this.componentFamilies, component.getFamily());
	}
	
	public void apply( FaceletContext ctx, UIComponent component, UIComponent parent ) {
		this.method.invoke( ctx, new Object[] {component, parent} );
	}
	
	public static ComponentGroup getComponentGroup( FaceletContext ctx,  UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		return (ComponentGroup) root.getAttributes().get( CURRENT_COMPONENT_GROUP );		
	}
	
}