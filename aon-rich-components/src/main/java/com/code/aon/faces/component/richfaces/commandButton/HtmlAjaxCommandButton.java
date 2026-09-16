package com.code.aon.faces.component.richfaces.commandButton;

import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import com.esferalia.aon.watson.util.AonStringUtils;

public class HtmlAjaxCommandButton extends org.ajax4jsf.component.html.HtmlAjaxCommandButton {
	
	
	private static final String DEFAULT_SYMBOL_COMMAND_BUTTON_CLASS = "material-symbols-outlined";

	@Override
	protected Renderer getRenderer(FacesContext context) {
		Renderer renderer = super.getRenderer(context);
		if ( renderCommandButtonSymbol(context, this) ) 
			return new HtmlAjaxCommandButtonSymbolRenderer(renderer);
		else 
			return renderer;
	}
	
	protected static boolean renderCommandButtonSymbol(FacesContext context, javax.faces.component.UIComponent component) {
		try {
			Boolean render = (Boolean) context.getApplication().evaluateExpressionGet(context, "#{renderCommandButtonSymbol}", Boolean.class);
			return Boolean.TRUE.equals(render);
		} catch ( Exception e ) {
			return false;
		}
	}

	protected static String getSymbolCommandButtonClass(FacesContext context, javax.faces.component.UIComponent component) {
		try {
			// An unresolvable variable is coerced to "" rather than raising, so check the value too.
			String symbolClass = (String) context.getApplication().evaluateExpressionGet(context, "#{symbolCommandButtonClass}", String.class);
			return AonStringUtils.isNotBlank(symbolClass) ? symbolClass : DEFAULT_SYMBOL_COMMAND_BUTTON_CLASS;
		} catch ( Exception e ) {
			return DEFAULT_SYMBOL_COMMAND_BUTTON_CLASS;
		}
	}
	
}
