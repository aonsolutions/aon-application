package com.code.aon.faces.component.richfaces.selectOneMenu;

import javax.faces.component.UIComponent;

import com.code.aon.faces.component.HTML;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class SelectOneMenuHandler extends AonComponentHandler{

    private static final String STYLE_CLASS = "aon-selectOneMenu";

	public SelectOneMenuHandler(ComponentConfig config) {
		super(config);
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
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (UIComponent) instance, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
	}
}