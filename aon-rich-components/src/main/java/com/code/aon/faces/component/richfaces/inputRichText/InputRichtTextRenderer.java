package com.code.aon.faces.component.richfaces.inputRichText;

import java.util.LinkedHashSet;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.ajax4jsf.renderkit.UserResourceRenderer;

import com.sun.faces.renderkit.html_basic.TextareaRenderer;

public class InputRichtTextRenderer extends TextareaRenderer implements UserResourceRenderer {

	private static final String FCK_EDITOR_JS = "/FCKeditor/fckeditor.js";
	
	private static final String FCK_EDITOR_AJAX_JS = "/com/code/aon/faces/component/richfaces/inputRichText/fckeditorAjax.js";
	
	@Override
	public LinkedHashSet<String> getHeaderScripts(FacesContext context,
			UIComponent component) {
		LinkedHashSet<String> uris = new LinkedHashSet<String>();
		uris.add(InputRichTextUtil.internalPath(FCK_EDITOR_JS));
		uris.add(InputRichTextUtil.internalPath(FCK_EDITOR_AJAX_JS));
		return uris;
	}

	@Override
	public LinkedHashSet<String> getHeaderStyles(FacesContext context,
			UIComponent component) {
		return null;
	}
	
}
