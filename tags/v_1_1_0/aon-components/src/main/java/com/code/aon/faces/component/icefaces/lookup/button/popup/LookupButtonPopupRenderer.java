package com.code.aon.faces.component.icefaces.lookup.button.popup;

import java.io.IOException;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import com.code.aon.faces.component.tomahawk.HtmlRenderer;

public class LookupButtonPopupRenderer extends HtmlRenderer {

	private static final Logger LOGGER = Logger.getLogger(LookupButtonPopupRenderer.class.getName());
	
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
    	LOGGER.info( "encodeBegin" );
    }

    public void decode(FacesContext context, UIComponent component)  {
    	LOGGER.info( "decode" );
    }

}
