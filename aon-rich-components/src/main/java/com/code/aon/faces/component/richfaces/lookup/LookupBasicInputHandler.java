package com.code.aon.faces.component.richfaces.lookup;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

/**
 * The Class TabbedPaneComponentHandler.
 * 
 * @author atellitu
 */
public class LookupBasicInputHandler extends AonAjaxInputHandler implements ILookupConstants {

   	/**
	 * The Constructor.
	 * 
	 * @param config the config
	 */
	public LookupBasicInputHandler(ComponentConfig config) {
		super( config );
		getAttribute(PROPERTY);
	}
	
	/**
	 * Sets the attributes.
	 * 
	 * @param instance the instance
	 * @param ctx the ctx
	 */
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		HtmlLookupBasicInput text = (HtmlLookupBasicInput) instance;
		LookupButtonHandler.updateProperty(ctx, tag, text);
		LookupButtonHandler.setLookupChangeListener(ctx, tag, text);
	}
	
	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		HtmlLookupBasicInput text = (HtmlLookupBasicInput) c;		
		if ( c.isRendered() ) {
			onComponentPopulated( ctx, text );
		}
	}
	
	protected void onComponentPopulated(FaceletContext ctx, HtmlLookupBasicInput text) {
	}
	
}