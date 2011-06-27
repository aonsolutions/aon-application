package com.code.aon.faces.component.richfaces.inputRichText;

import javax.faces.context.ExternalContext;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlEditor;

import com.code.aon.faces.component.AonComponentHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class InputRichTextHandler extends AonComponentHandler {
	
	public InputRichTextHandler(ComponentConfig config) {
		super(config);
	}
	
	private boolean isAppleDevice( FaceletContext ctx ) {
		ExternalContext ectx = ctx.getFacesContext().getExternalContext();
		String userAgent = ectx.getRequestHeaderMap().get("user-agent");
		return StringUtils.contains(userAgent, "iPad") ||
				StringUtils.contains(userAgent, "iPod") ||
				StringUtils.contains(userAgent, "iPhone");
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		if ( isAppleDevice(ctx) ) {
			HtmlEditor editor = (HtmlEditor) instance;
			editor.setViewMode("source");
			editor.setWidth(500);
		}
	}

}
