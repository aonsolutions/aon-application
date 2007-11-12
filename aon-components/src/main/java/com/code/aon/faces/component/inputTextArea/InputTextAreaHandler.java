package com.code.aon.faces.component.inputTextArea;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlInputTextarea;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class InputTextAreaHandler extends AonComponentHandler{

    private static final String STYLE_CLASS = "aon-form-input-text";

	public InputTextAreaHandler(ComponentConfig config) {
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
		((HtmlInputTextarea) instance).setPartialSubmit( true );
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (HtmlInputTextarea) instance, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
	}
}