package com.code.aon.faces.component.richfaces.componentGroup;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class ComponentGroup {
	
	public static final String CURRENT_COMPONENT_GROUP = "com.code.aon.faces.ComponentGroup.current";

	private List<ComponentGroupMethod> componentGroupMethods;

	public ComponentGroup( ComponentGroupMethod cgm ) {
		this.componentGroupMethods = new LinkedList<ComponentGroupMethod>();
		add(cgm);
	}
	
	public void add( ComponentGroupMethod cgm ) {
		this.componentGroupMethods.add( cgm );
	}

	public void apply( FaceletContext ctx, UIComponent component, UIComponent parent ) {
		for( ComponentGroupMethod cgm : this.componentGroupMethods ) {
			if ( cgm.isAppicable(component) ) {
				cgm.apply(ctx, component, parent);		
			}
		}
	}
	
	public static ComponentGroup getComponentGroup( FaceletContext ctx,  UIComponent component ) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		return (ComponentGroup) root.getAttributes().get( CURRENT_COMPONENT_GROUP );		
	}
	
}