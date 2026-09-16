package com.code.aon.faces.component.richfaces.commandButton;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import jakarta.el.ValueExpression;

public class HtmlAjaxCommandButton extends org.ajax4jsf.component.html.HtmlAjaxCommandButton {
	
	private String symbol = null;
	
	@Override
	protected Renderer getRenderer(FacesContext context) {
		Renderer renderer = super.getRenderer(context);
		if ( isSymbolDefined(context, this) ) 
			return new HtmlAjaxCommandButtonSymbolRenderer(renderer);
		else 
			return renderer;
	}
	
	private boolean isSymbolDefined(FacesContext context, javax.faces.component.UIComponent component) {
		try {
			return (Boolean) context.getApplication().evaluateExpressionGet(context, "#{commandButtonSymbol}", Boolean.class);
		} catch ( Exception e ) {
			return false;
		}
		
	}
	
    public String getSymbol()
    {
        if (symbol != null) 
        	return symbol;
        
        ValueExpression valueExpression = getValueExpression("symbol");
        return valueExpression != null ? valueExpression.getValue(getFacesContext().getELContext()) : null;
    }	
}
