package com.code.aon.faces.component.selectOneRadio;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlSelectOneRadio;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class SelectOneRadioHandler extends AonComponentHandler{

    private static final String STYLE_CLASS = "aon-selectOneRadio";

	public SelectOneRadioHandler(ComponentConfig config) {
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
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (HtmlSelectOneRadio) instance, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
		if (! hasValue(ctx, PARTIAL_SUBMIT ) ) {
			UIComponentTagUtils.setBooleanProperty(ctx.getFacesContext(), (HtmlSelectOneRadio) instance, PARTIAL_SUBMIT, FALSE );			
		}
	}
}