package com.code.aon.faces.component.richfaces.lookup;

import static com.code.aon.faces.component.util.HTML.DISABLED_ATTR;

import java.io.IOException;

import jakarta.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.lookup.button.LookupButtonHandler;
import com.code.aon.faces.component.util.FaceletUtil;
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
	protected void applyNextHandler(FaceletContext ctx, UIComponent c)
			throws IOException, FacesException, ELException {
		HtmlLookupBasicInput text = (HtmlLookupBasicInput) c;
		if ( text.isRendered() ) {
			updateAttributes(ctx, text);	
		}
		super.applyNextHandler(ctx, c);
	}
	
	protected void updateAttributes(FaceletContext ctx, HtmlLookupBasicInput text) {
		boolean disabled = false;
		if ( text.isResolved() ) {
			disabled = true;
		} else if ( FaceletUtil.hasValue(ctx, tag, DISABLED_ATTR) ) {
			disabled = getAttribute(DISABLED_ATTR).getBoolean(ctx);
		}
		text.setDisabled(disabled);
	}
	
}