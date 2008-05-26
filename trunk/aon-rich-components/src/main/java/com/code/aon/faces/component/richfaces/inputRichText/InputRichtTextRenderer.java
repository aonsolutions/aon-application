package com.code.aon.faces.component.richfaces.inputRichText;

import java.io.IOException;
import java.util.LinkedHashSet;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.renderkit.UserResourceRenderer;
import org.apache.commons.lang.StringUtils;

import com.sun.faces.renderkit.html_basic.TextareaRenderer;

public class InputRichtTextRenderer extends TextareaRenderer implements UserResourceRenderer {

	public static final String CUSTOM_CONFIGURATION_PATH = "org.fckfaces.CUSTOM_CONFIGURATIONS_PATH";
		
	@Override
	public LinkedHashSet<String> getHeaderScripts(FacesContext context,
			UIComponent component) {
		LinkedHashSet<String> uris = new LinkedHashSet<String>();
		uris.add(InputRichTextUtil.internalPath("/FCKeditor/fckeditor.js"));
		return uris;
	}

	@Override
	public LinkedHashSet<String> getHeaderStyles(FacesContext context,
			UIComponent component) {
		return null;
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeEnd(context, component);
		
		InputRichTextComponent irt = (InputRichTextComponent) component;
		
		ResponseWriter writer = context.getResponseWriter();
		
		//Initial Configuration
		final ExternalContext external = context.getExternalContext();
		String cstConfigPathParam = external.getInitParameter(CUSTOM_CONFIGURATION_PATH);
		
		writer.startElement("script", component.getParent());
		
		String toolBar = "Default";
		if(StringUtils.isNotBlank(irt.getToolbarSet())) {
			toolBar = irt.getToolbarSet();
		}
		
		String heightJS = "";
		String widthJS = "";
		String configPathJS = "";
		
		if(StringUtils.isNotBlank(irt.getHeight())) {
			heightJS = "oFCKeditor.Height = '" + irt.getHeight() + "';\r\n";
		}
		
		if(StringUtils.isNotBlank(irt.getWidth())) {
			widthJS = "oFCKeditor.Width = '" + irt.getWidth() + "';\r\n";
		}
		
		if (StringUtils.isNotBlank(cstConfigPathParam) ) {
			cstConfigPathParam = InputRichTextUtil.externalPath(cstConfigPathParam);
			configPathJS = "   oFCKeditor.Config['CustomConfigurationsPath']='"+cstConfigPathParam+"';\r\n";
		}

		String js = 
		"function applyEditor" + component.getId() +"() {" +
		"	var sBasePath = '" + InputRichTextUtil.internalPath("/FCKeditor/") + "';\r\n" +
		"	var sTextAreaName = '" + component.getClientId(context) + "';\r\n" +
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
		"applyEditor" + component.getId() +"();";
		
		writer.writeText(js, null);
		writer.endElement("script");
	}
	
}
