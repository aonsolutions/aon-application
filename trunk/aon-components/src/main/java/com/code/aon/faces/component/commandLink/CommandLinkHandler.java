package com.code.aon.faces.component.commandLink;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlCommandLink;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class CommandLinkHandler extends AonComponentHandler{

    private static final String STYLE_CLASS = "aon-navigation-link";

	public CommandLinkHandler(ComponentConfig config) {
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
		((HtmlCommandLink) instance).setPartialSubmit( true );
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (HtmlCommandLink) instance, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
	}

}
