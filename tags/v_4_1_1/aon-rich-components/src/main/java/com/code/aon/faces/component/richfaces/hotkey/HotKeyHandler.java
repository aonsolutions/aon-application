package com.code.aon.faces.component.richfaces.hotkey;

import javax.el.ValueExpression;

import org.apache.commons.lang.StringUtils;
import org.richfaces.component.html.HtmlHotKey;

import com.code.aon.faces.component.AonComponentHandler;
import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.Metadata;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentConfig;

public class HotKeyHandler extends AonComponentHandler {

	private static String ELEMENT_ID = "elementId";
	
	private static String HANDLER_EX = "handlerEx";
	
	private final TagAttribute elementId;
	
	private final TagAttribute handlerEx;
	
	public HotKeyHandler(ComponentConfig config) {
		super( config );
		this.elementId = this.getRequiredAttribute(ELEMENT_ID);
		this.handlerEx = this.getRequiredAttribute(HANDLER_EX);
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		return set.ignore(ELEMENT_ID).ignore(HANDLER_EX);
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		String idValue = elementId.getValue(ctx);
		String expr = "#{rich:element('" + idValue + "')}";
		String handler = handlerEx.getValue(ctx);
		handler = StringUtils.replace( handler, "{element}", expr );	
		HtmlHotKey hotKey = (HtmlHotKey) instance;
		ValueExpression ve = FaceletUtil.getValueExpression(ctx, handler, Object.class);
		hotKey.setValueExpression( "handler", ve);
	}
	
}
