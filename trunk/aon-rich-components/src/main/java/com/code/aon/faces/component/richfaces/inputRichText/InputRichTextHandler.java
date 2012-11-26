package com.code.aon.faces.component.richfaces.inputRichText;

import org.richfaces.component.html.HtmlEditor;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.ui.util.AonUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class InputRichTextHandler extends AonComponentHandler {
	
	public InputRichTextHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		if ( AonUtil.isAppleDevice() ) {
			HtmlEditor editor = (HtmlEditor) instance;
			editor.setViewMode("source");
			editor.setWidth(500);
		}
	}

}
