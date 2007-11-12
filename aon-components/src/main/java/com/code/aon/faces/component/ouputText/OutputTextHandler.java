package com.code.aon.faces.component.ouputText;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlOutputText;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class OutputTextHandler extends AonComponentHandler {

	private static final String OUTPUT_TEXT_STYLE_CLASS = "aon-outputText";
	
	public OutputTextHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		HtmlOutputText text = (HtmlOutputText) instance;
		super.setAttributes(ctx, instance);
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), text, HTML.STYLE_CLASS_ATTR, OUTPUT_TEXT_STYLE_CLASS );
		}
	}
}
