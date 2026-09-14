package com.code.aon.faces.component.richfaces.commandButton;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

public class HtmlAjaxCommandButton extends org.ajax4jsf.component.html.HtmlAjaxCommandButton {
	
	@Override
	protected Renderer getRenderer(FacesContext context) {
		Renderer renderer = super.getRenderer(context);
		return new HtmlAjaxCommandButtonRenderer(renderer);
	}

}
