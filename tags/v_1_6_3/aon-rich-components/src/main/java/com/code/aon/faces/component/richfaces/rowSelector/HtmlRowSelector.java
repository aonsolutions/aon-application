package com.code.aon.faces.component.richfaces.rowSelector;

import org.ajax4jsf.component.html.HtmlAjaxSupport;

public class HtmlRowSelector extends HtmlAjaxSupport {

    public static final String COMPONENT_TYPE = "com.code.aon.faces.RowSelector";

    public static final String RENDERER_TYPE = "com.code.aon.faces.RowSelectorRenderer";
    
	public HtmlRowSelector() {
        setRendererType(RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_TYPE;
	}
    
	
	
}
