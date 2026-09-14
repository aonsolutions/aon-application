package com.code.aon.faces.component.richfaces.commandButton;

import java.io.IOException;
import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.el.ValueExpression;

public class HtmlAjaxCommandButtonRenderer extends Renderer {
	
	private String symbol;
	private Renderer delegate;

	public HtmlAjaxCommandButtonRenderer(Renderer delegate) {
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
		
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("span", component);
		
		symbol = getSymbol(context, component);
		if ( AonStringUtils.isNotBlank(symbol) )
			delegate.encodeBegin(context, component);
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		delegate.encodeEnd(context, component);
		ResponseWriter writer = context.getResponseWriter();
		
		if ( AonStringUtils.isNotBlank(symbol) ) {
			writer.startElement("i", component);
			writer.writeText(symbol, null);
			writer.endElement("i");
			writer.endElement("span");
		}

	}
	
	private String getSymbol(FacesContext context, UIComponent component) {
		return getAttribute(context, component, "symbol").orElse(null);
	}
	private Optional<String> getAttribute(FacesContext context, UIComponent component, String name) {
		try {
			ValueExpression valueExpression = component.getValueExpression(name);
			if ( valueExpression == null )
				return Optional.empty();
			return valueExpression.getValue(context.getELContext());
		} catch ( Throwable t ) {
			return Optional.empty();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
