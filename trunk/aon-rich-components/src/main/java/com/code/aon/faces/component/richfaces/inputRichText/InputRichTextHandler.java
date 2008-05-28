package com.code.aon.faces.component.richfaces.inputRichText;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;

import org.apache.commons.lang.StringUtils;

import com.code.aon.faces.component.richfaces.AonAjaxInputHandler;
import com.code.aon.faces.component.richfaces.RegionableInputHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class InputRichTextHandler extends AonAjaxInputHandler {
	
	private static final String CUSTOM_CONFIGURATION_PATH = "org.fckfaces.CUSTOM_CONFIGURATIONS_PATH";
	
	private static final String TEMPLATE_PATH = "com/code/aon/faces/component/richfaces/inputRichText/";

	private static final String TEMPLATE = TEMPLATE_PATH + "template.xhtml";
	
	private static final String SCRIPT = "inputRichTextScript";
	
	public InputRichTextHandler(ComponentConfig config) {
		super(config);
	}

	private void appendParameter( StringBuffer sb, String value, String _default ) {
		if (StringUtils.isNotBlank(value) ) {
			sb.append('\'').append(value).append('\'');
		} else if ( _default != null ) {
			sb.append('\'').append(_default).append('\'');
		} else {
			sb.append("null");
		}
	}
	
	private String getScript( FaceletContext ctx, InputRichTextComponent irt ) {
		//Initial Configuration
		final ExternalContext external = ctx.getFacesContext().getExternalContext();
		String cstConfigPathParam = null;
		String param = external.getInitParameter(CUSTOM_CONFIGURATION_PATH);
		if (StringUtils.isNotBlank(param) ) {
			cstConfigPathParam = InputRichTextUtil.externalPath(param);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("applyEditor( ");
		
		appendParameter(sb, InputRichTextUtil.internalPath("/FCKeditor/"), null);
		sb.append(", ");

		appendParameter(sb, irt.getClientId(ctx.getFacesContext()), null);
		sb.append(", ");
		
		appendParameter(sb, cstConfigPathParam, null);
		sb.append(", ");
		
		appendParameter(sb, irt.getToolbarSet(), "Default");
		sb.append(", ");
		
		appendParameter(sb, irt.getHeight(), null);
		sb.append(", ");
		
		appendParameter(sb, irt.getWidth(), null);
		sb.append(");");
		
		return sb.toString();
	}
	
	private void insertTemplate(FaceletContext ctx, UIComponent component) {
		InputRichTextComponent irt = (InputRichTextComponent) component;
		VariableMapper newMapper = new VariableMapperWrapper(ctx.getVariableMapper());
		ValueExpression script = ctx.getExpressionFactory().createValueExpression(ctx, getScript(ctx, irt), String.class);
		newMapper.setVariable(SCRIPT, script);
		FaceletUtil.insertTemplate(ctx, tag, component, FaceletUtil.getTemplate(TEMPLATE), newMapper);
	}
	
	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		insertTemplate( ctx, c );		
	}
	
}
