package com.code.aon.faces.component.icefaces.inputRichText;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.lang.StringUtils;

import com.icesoft.faces.component.ext.HtmlInputTextarea;

/**
 * 
 * @author srecinto
 *
 */
public class InputRichTextComponent extends HtmlInputTextarea {
	public static final String CUSTOM_CONFIGURATION_PATH = "org.fckfaces.CUSTOM_CONFIGURATIONS_PATH";

	private String toolbarSet;
	private String height;
	private String width;

	/**
	 * 
	 */
	public String getComponentType() { 
		return com.icesoft.faces.component.ext.HtmlInputTextarea.COMPONENT_TYPE;
		
	}
	
	/**
	 * 
	 */
	public String getRendererType() { 
		return com.icesoft.faces.component.ext.HtmlInputTextarea.RENDERER_TYPE;
	} 
	
	/**
	 * 
	 * @param context
	 * @throws IOException
	 */
	public void encodeBegin(FacesContext context) throws IOException {
		super.encodeBegin(context);
		
		
	}
	
	/**
	 * Moved to encode end so that the inline java script will run after the textArea was rendered before this script is run
	 * @param context
	 * @throws IOException
	 */
	public void encodeEnd(FacesContext context) throws IOException {
		super.encodeEnd(context);
		
		ResponseWriter writer = context.getResponseWriter();
		
		//Initial Configuration
		final ExternalContext external = context.getExternalContext();
		String cstConfigPathParam = external.getInitParameter(CUSTOM_CONFIGURATION_PATH);
		
		//Initial JS link
		writer.startElement("script", this.getParent());
		writer.writeAttribute("type", "text/javascript", null);
		writer.writeAttribute("src", InputRichTextUtil.internalPath("/FCKeditor/fckeditor.js"), null);
		writer.endElement("script");
		
		writer.startElement("script", this.getParent());
		
		String toolBar = "Default";
		if(StringUtils.isNotBlank(toolbarSet)) {
			toolBar = toolbarSet;
		}
		
		String heightJS = "";
		String widthJS = "";
		String configPathJS = "";
		
		if(StringUtils.isNotBlank(height)) {
			heightJS = "oFCKeditor.Height = '" + height + "';\r\n";
		}
		
		if(StringUtils.isNotBlank(width)) {
			widthJS = "oFCKeditor.Width = '" + width + "';\r\n";
		}
		
		if (StringUtils.isNotBlank(cstConfigPathParam) ) {
			cstConfigPathParam = InputRichTextUtil.externalPath(cstConfigPathParam);
			configPathJS = "   oFCKeditor.Config['CustomConfigurationsPath']='"+cstConfigPathParam+"';\r\n";
		}

		String js = 
		"function applyEditor" + this.getId() +"() {" +
		"	var sBasePath = '" + InputRichTextUtil.internalPath("/FCKeditor/") + "';\r\n" +
		"	var sTextAreaName = '" + this.getClientId(context) + "';\r\n" +
		"	var oFCKeditor = new FCKeditor( sTextAreaName ) ;\r\n" + 
		configPathJS +
		"	oFCKeditor.BasePath	= sBasePath ;\r\n" +
		"	oFCKeditor.ToolbarSet='" + toolBar + "';\r\n" +
		heightJS +
		widthJS + 
		"	oFCKeditor.ReplaceTextarea(); \r\n" +
		"	var oTextbox = document.getElementById(sTextAreaName);\r\n" +
		"	if(oTextbox.hasChildNodes()) {\r\n" +
		"		var oTextNode;\r\n" +
		"		var oParentNode = oTextbox.parentNode;\r\n" +
		"		if(oTextbox.childNodes.length > 1) {\r\n" +
		"			for(var i = 0; i < oTextbox.childNodes.length; i++) {\r\n" +
		"				if(oTextbox.childNodes.item(i).nodeType != 3 ) { //Not a Text node\r\n" +
		"					oParentNode.appendChild(oTextbox.removeChild(oTextbox.childNodes.item(i)));\r\n" +
		"					i = i - 1;\r\n" +
		"				}\r\n" +
		"			}\r\n" +
		"		}\r\n" +
		"	}\r\n" +
		"}" +
		"applyEditor" + this.getId() +"();";
		
		writer.writeText(js, null);
		writer.endElement("script");
	}
	
	/**
	 * 
	 * @return
	 */
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	public Object saveState(FacesContext context) {
		Object values[] = new Object[2];
		values[0] = super.saveState(context);
		values[1] = toolbarSet;
		
		return values;
	}
	
	public void restoreState(FacesContext context, Object state) {
		Object values[] = (Object[]) state;
		super.restoreState(context, values[0]);
		this.toolbarSet = (String)values[1];
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
