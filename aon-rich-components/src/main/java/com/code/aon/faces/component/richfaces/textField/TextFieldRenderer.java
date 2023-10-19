package com.code.aon.faces.component.richfaces.textField;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

public class TextFieldRenderer extends Renderer {
    
    private Renderer renderer;
    
    
    public TextFieldRenderer(Renderer renderer) {
	this.renderer = renderer;
    }
    
    @Override
    public boolean getRendersChildren() {
	return renderer.getRendersChildren();
    }
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
	renderer.decode(context, component);
    }

    @Override
    public String convertClientId(FacesContext context, String clientId) {
	return renderer.convertClientId(context, clientId);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {

	String clientId = component.getClientId(context);
	String title = convertToString(component.getAttributes().get("title"));
	boolean required = convertToBoolean(component.getAttributes().get("required"));
	String mainStyleClass = convertToString(component.getAttributes().get("mainStyleClass"));
	
	
	ResponseWriter writer = context.getResponseWriter();
	writer.startElement("div", component);
	writer.writeAttribute("class", mainStyleClass, null);
	writer.writeAttribute("onclick", "javascript:document.getElementById('" + clientId + "').focus()", null);
	
	writer.startElement("div", component);
	writer.write(title);
	if ( required ) {
	    writer.write(" * ");
	}
	writer.endElement("div");
	
	
	renderer.encodeBegin(context, component);
    }

    @Override
    public void encodeChildren(FacesContext arg0, UIComponent arg1) throws IOException {
	renderer.encodeChildren(arg0, arg1);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
	renderer.encodeEnd(context, component);

	ResponseWriter writer = context.getResponseWriter();
	writer.endElement("div");
    }

    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue)
	    throws ConverterException {
	return renderer.getConvertedValue(context, component, submittedValue);
    }

    private String convertToString(Object obj ) {
        return ( obj == null ? "" : obj.toString() );
    }

    private boolean convertToBoolean(Object obj ) {
        return Boolean.valueOf(obj.toString());
    }
}
