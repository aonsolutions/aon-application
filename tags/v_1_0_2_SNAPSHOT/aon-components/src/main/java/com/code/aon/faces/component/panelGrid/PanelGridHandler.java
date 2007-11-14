package com.code.aon.faces.component.panelGrid;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.myfaces.UIComponentTagUtils;
import com.icesoft.faces.component.ext.HtmlPanelGrid;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class PanelGridHandler extends AonComponentHandler {

	private static final String STYLE_CLASS = "aon-panelGrid";

	public PanelGridHandler(ComponentConfig config) {
		super(config);
	}
	
	@Override
	protected void setAttributes(FaceletContext ctx, Object instance) {
		super.setAttributes(ctx, instance);
		if (! hasValue(ctx, HTML.STYLE_CLASS_ATTR) ) {
			UIComponentTagUtils.setStringProperty(ctx.getFacesContext(), (HtmlPanelGrid) instance, HTML.STYLE_CLASS_ATTR, STYLE_CLASS );
		}
	}
}