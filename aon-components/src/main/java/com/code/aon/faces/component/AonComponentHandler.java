package com.code.aon.faces.component;

import java.net.URL;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

import com.code.aon.faces.component.util.FaceletUtil;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class AonComponentHandler extends ComponentHandler {

    private ComponentConfig config;
    
    /**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public AonComponentHandler( ComponentConfig config) {
		super(config);
		this.config = config;
	}
	
	public ComponentConfig getConfig() {
		return config;
	}

	protected void insertTemplate(FaceletContext ctx, UIComponent parent, URL template, VariableMapper newMapper ) {
		FaceletUtil.insertTemplate(ctx, this.tag, parent, template, newMapper);
	}
	
	public static String appendExpression(String expression, String value) {
		StringBuffer sb = new StringBuffer(expression);
		int offset = sb.length() - 1;
		sb.insert(offset++, '.');
		sb.insert(offset, value);
		return sb.toString();
	}
	
	public boolean hasValue(FaceletContext ctx, String name) {
		return FaceletUtil.hasValue(ctx, tag, name);
	}	

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		ComponentManager.getInstance().updateMetaRuleset( tag, set );
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		ComponentManager.getInstance().setAttributes( tag, ctx, (UIComponent) instance );
	}
	
}
