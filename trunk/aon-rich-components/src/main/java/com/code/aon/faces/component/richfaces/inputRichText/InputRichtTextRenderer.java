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

	private static final String CUSTOM_CONFIGURATION_PATH = "org.fckfaces.CUSTOM_CONFIGURATIONS_PATH";
	
	private static final String FCK_EDITOR_JS = "/FCKeditor/fckeditor.js";
	
	private static final String FCK_EDITOR_AJAX_JS = "/com/code/aon/faces/component/richfaces/inputRichText/fckeditorAjax.js";
	
	@Override
	public LinkedHashSet<String> getHeaderScripts(FacesContext context,
			UIComponent component) {
		LinkedHashSet<String> uris = new LinkedHashSet<String>();
		uris.add(InputRichTextUtil.internalPath(FCK_EDITOR_JS));
		uris.add(InputRichTextUtil.internalPath(FCK_EDITOR_AJAX_JS));
		return uris;
	}

	@Override
	public LinkedHashSet<String> getHeaderStyles(FacesContext context,
			UIComponent component) {
		return null;
	}
	
	private String getCustomConfigurationsPath(FacesContext context) {
		ExternalContext external = context.getExternalContext();
		String value = null;
		String param = external.getInitParameter(CUSTOM_CONFIGURATION_PATH);
		if (StringUtils.isNotBlank(param) ) {
			value = InputRichTextUtil.externalPath(param);
		}
		return value;
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component)
			throws IOException {
		super.encodeEnd(context, component);
		
		InputRichTextComponent irt = (InputRichTextComponent) component;
		
		ResponseWriter writer = context.getResponseWriter();
		
		String customConfigurationsPath = getCustomConfigurationsPath(context);
		String toolBar = StringUtils.defaultIfEmpty(irt.getToolbarSet(), "Default");
		
		StringBuffer sb = new StringBuffer();
		sb.append( "try {\r\n" );
		sb.append( "\tvar oFCKeditor = new FCKeditor( '" ).append( component.getClientId(context) ).append( "' ) ;\r\n" );
		sb.append( "\toFCKeditor.BasePath = '" ).append( InputRichTextUtil.internalPath("/FCKeditor/") ).append( "';\r\n" );
		if ( customConfigurationsPath != null ) {
			sb.append( "\toFCKeditor.Config['CustomConfigurationsPath']='" ).append( customConfigurationsPath ).append( "';\r\n" );
		}
		if (! StringUtils.isEmpty(irt.getHeight()) ) {
			sb.append( "\toFCKeditor.Height = " ).append( irt.getHeight() ).append( ";\r\n" );
		}
		if (! StringUtils.isEmpty(irt.getWidth()) ) {
			sb.append( "\toFCKeditor.Width = " ).append( irt.getWidth() ).append( ";\r\n" );
		}
		sb.append( "\toFCKeditor.ToolbarSet= '" ).append( toolBar ).append( "';\r\n" );
		sb.append( "\toFCKeditor.ReplaceTextarea();\r\n" );
		sb.append( "} catch ( e ) {\r\n" );
		sb.append( "\talert( e );\r\n" );
		sb.append( "}\r\n" );
		
		writer.startElement("script", component.getParent());
		writer.writeText(sb.toString(), null);
		writer.endElement("script");
	}	
	
}
