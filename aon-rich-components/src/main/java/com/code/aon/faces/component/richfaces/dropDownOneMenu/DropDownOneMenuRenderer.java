package com.code.aon.faces.component.richfaces.dropDownOneMenu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;

public class DropDownOneMenuRenderer extends Renderer {
    
    private Renderer renderer;
    
    
    public DropDownOneMenuRenderer(Renderer renderer) {
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

	String title = (String) component.getAttributes().get("title");
	String mainStyleClass = convertToString(component.getAttributes().get("mainStyleClass"));
	String id = component.getClientId(context);
	
	
	ResponseWriter writer = context.getResponseWriter();
	writer.startElement("div", component);
        writer.writeAttribute("id", id + ":Panel","id");
	writer.writeAttribute("class", mainStyleClass, null);
	
	writer.startElement("div", component);
	writer.write(title);
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

}
