package com.code.aon.faces.component.richfaces.inputRichText;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;

import org.ajax4jsf.component.html.AjaxForm;
import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.form.FormHandler;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentSupport;

public class InputRichTextHandler extends AonAjaxInputHandler {

	public InputRichTextHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	protected void onComponentCreated(FaceletContext ctx, UIComponent component,
			UIComponent parent) {
		UIViewRoot root = ComponentSupport.getViewRoot(ctx, component);
		AjaxForm form = (AjaxForm) root.getAttributes().get( FormHandler.CURRENT_FORM );
		if ( form != null ) {
			String textId = component.getClientId(ctx.getFacesContext());
			String onsubmit = "updateEditorContent('" + textId + "');";
			String oldValue = form.getOnsubmit();
			if (! StringUtils.isEmpty(oldValue) ) {
				onsubmit += oldValue;
			}
			form.setOnsubmit(onsubmit);
		}
	}
	
}
