package com.code.aon.faces.component.richfaces.rowSelector;

import org.ajax4jsf.renderkit.html.AjaxSupportRenderer;
import org.ajax4jsf.resource.InternetResource;

public class RowSelectorRenderer extends AjaxSupportRenderer {

	private static final String JAVASCRIPT = "/rowSelector/rowSelector.js";

	@Override
	protected InternetResource[] getAdditionalScripts() {
		InternetResource[] scripts = null; 
		InternetResource[] additionalScripts = super.getAdditionalScripts();
		if (null != additionalScripts) {
			scripts = new InternetResource[additionalScripts.length + 1];
			System.arraycopy(additionalScripts, 0, scripts, 1, additionalScripts.length);
		} else {
			scripts = new InternetResource[1];
		}
		scripts[0] = getResource(JAVASCRIPT);
		return scripts;
	}

}
