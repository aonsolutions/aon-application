package com.code.aon.faces.component.richfaces.commandButton;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import com.esferalia.aon.watson.util.AonStringUtils;

public class HtmlAjaxCommandButtonSymbolRenderer extends Renderer {
	
	private static final Pattern AON_ICON_CLASS = Pattern.compile("aon[-_]icon[-_](?<symbol>[^\\s]+)");
	
	private Renderer delegate;

	public HtmlAjaxCommandButtonSymbolRenderer(Renderer delegate) {
		super();
		this.delegate = delegate;
	}
	
	@Override
	public String convertClientId(FacesContext context, String clientId) {
		return delegate.convertClientId(context, clientId);
	}

	@Override
	public void decode(FacesContext context, UIComponent component) {
		delegate.decode(context, component);
	}

	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		delegate.encodeChildren(context, component);
	}

	@Override
	public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
			throws ConverterException {
		return delegate.getConvertedValue(context, component, submittedValue);
	}

	@Override
	public boolean getRendersChildren() {
		return delegate.getRendersChildren();
	}
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		
		
		if ( isSymbolDefined(context, component) ) {
			ResponseWriter writer = context.getResponseWriter();
			writer.startElement("span", component);
			writer.writeAttribute("class", "aon-symbol-container", null);
		}
		
		delegate.encodeBegin(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		delegate.encodeEnd(context, component);
		if ( isSymbolDefined(context, component) ) {
			writeSymbol(context, component);
			ResponseWriter writer = context.getResponseWriter();
			writer.endElement("span");
		}
		
	}
	
	private void writeSymbol(FacesContext context, UIComponent component) throws IOException {
		Optional<String> symbol = getSymbol(context, component);
		if (symbol.isEmpty())
			return;
		// Write the symbol inside an <i> tag, with the class "aon-symbol"
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("i", component);
		String symbolCommandButtonClass = HtmlAjaxCommandButton.getSymbolCommandButtonClass(context, component);
		writer.writeAttribute("class", symbolCommandButtonClass, null);
		writer.writeText(symbol.get() , null);
		writer.endElement("i");
	}
	
	private boolean isSymbolDefined(FacesContext context, UIComponent component) {
		return isAonIconClassDefined(context, component);
	}
	
	private Optional<String> getSymbol(FacesContext context, UIComponent component) {
		return getAonIconClassSymbol(context, component);
	}
	
	private boolean isAonIconClassDefined(FacesContext context, UIComponent component) {
		if ( component instanceof HtmlAjaxCommandButton commandButton ) {
			String styleClass = commandButton.getStyleClass();
			if ( AonStringUtils.isNotBlank(styleClass) ) {
				return AON_ICON_CLASS.matcher(styleClass).find();
			}	
		}
		return false;
	}

	private Optional<String> getAonIconClassSymbol(FacesContext context, UIComponent component) {
		if ( component instanceof HtmlAjaxCommandButton commandButton ) {
			String styleClass = commandButton.getStyleClass();
			if ( AonStringUtils.isNotBlank(styleClass) ) {
				Matcher matcher = AON_ICON_CLASS.matcher(styleClass);
				if ( matcher.find() ) {
					return Optional.of(matcher.group()).map( aonIconClass -> getIconClassSymbolMap(context, component).getOrDefault(aonIconClass, matcher.group("symbol")) );
				}
			}	
		}
		return Optional.empty();		
	}
	
	private Map<String,String> getIconClassSymbolMap(FacesContext context, javax.faces.component.UIComponent component) {
		try {
			// An unresolvable variable is coerced to null rather than raising, so guard the result.
			Map<String,String> iconClassSymbolMap = (Map<String,String>) context.getApplication().evaluateExpressionGet(context, "#{iconClassSymbolMap}", Map.class);
			return iconClassSymbolMap != null ? iconClassSymbolMap : Collections.emptyMap();
		} catch ( Exception e ) {
			return Collections.emptyMap();
		}
		
	}
	
	
}
