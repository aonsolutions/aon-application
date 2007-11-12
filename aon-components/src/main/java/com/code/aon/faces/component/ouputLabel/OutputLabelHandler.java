package com.code.aon.faces.component.ouputLabel;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlOutputLabel;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class OutputLabelHandler extends AonComponentHandler {

	private static final String OUTPUT_TEXT_STYLE_CLASS = "aon-outputLabel";
	
	public OutputLabelHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (HtmlOutputLabel) instance, HTML.STYLE_CLASS_ATTR, OUTPUT_TEXT_STYLE_CLASS );
		}
	}
}