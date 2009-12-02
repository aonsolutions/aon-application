package com.code.aon.faces.component.richfaces.inputRichText;

import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.context.FacesContext;

/**
 * 
 * @author srecinto
 *
 */
public class InputRichTextComponent extends HtmlInputTextarea {
	
	public static final String COMPONENT_TYPE = "com.code.aon.faces.InputRichText";
    public static final String RENDERER_TYPE = "com.code.aon.faces.InputRichTextRenderer";
    
	private String toolbarSet;
	private String height;
	private String width;
	
	public InputRichTextComponent() {
		setRendererType(RENDERER_TYPE);
	}

	/**
	 * 
	 */
	public String getComponentType() { 
		return COMPONENT_TYPE;	
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFamily() {
		return COMPONENT_TYPE;
	}
	
	public Object saveState(FacesContext context) {
		Object values[] = new Object[4];
		values[0] = super.saveState(context);
		values[1] = toolbarSet;
		values[2] = height;
		values[3] = width;
		
		return values;
	}
	
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.toolbarSet = (String)values[1];
		this.height = (String)values[2];
		this.width = (String)values[3];
	}

	public String getToolbarSet() {
		return toolbarSet;
	}

	public void setToolbarSet(String toolbarSet) {
		this.toolbarSet = toolbarSet;
	}
	
	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

}
