package com.code.aon.faces.component.richfaces.actionHotKey;

import jakarta.el.ValueExpression;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlHotKey;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class ActionHotKeyHandler extends AonComponentHandler {

	private static final String HANDLER_EX = "handlerEx";
	
	private static final String DEFAULT_HANDLER_EX = "{element}.onclick();";
	
	public ActionHotKeyHandler(ComponentConfig config) {
		super( config );
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		return set.ignore(HANDLER_EX);
	}

	@Override
	protected void onComponentPopulated(FaceletContext ctx, UIComponent c,
			UIComponent parent) {
		String parentId = parent.getClientId(ctx.getFacesContext());
		String expr = "document.getElementById('" + parentId + "')";
		String handler = DEFAULT_HANDLER_EX;
		TagAttribute handlerEx = getAttribute(HANDLER_EX);
		if ( handlerEx != null ) {
			handler = handlerEx.getValue(ctx);
		}
		handler = StringUtils.replace( handler, "{element}", expr );	
		HtmlHotKey hotKey = (HtmlHotKey) c;
		ValueExpression ve = FaceletUtil.getValueExpression(ctx, handler, Object.class);
		hotKey.setValueExpression( "handler", ve);
	}
	
}
