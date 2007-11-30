package com.code.aon.faces.component.icefaces.lookup.button.popup;

import javax.faces.webapp.UIComponentELTag;

public class LookupButtonPopupTag extends UIComponentELTag {
	
    public String getComponentType()
    {
        return LookupButtonPopup.COMPONENT_TYPE;
    }

    public String getRendererType()
    {
        return LookupButtonPopup.DEFAULT_RENDERER_TYPE;
    }
}
