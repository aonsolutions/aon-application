package com.code.aon.faces.component;

import java.net.URL;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public class AonComponentHandler extends ComponentHandler {

	private static final String COMPONENTS_RESOURCE = "components.xml";
	
	private static ComponentManager componentManager = new ComponentManager(COMPONENTS_RESOURCE);
	
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
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(newMapper);
		try {
			ctx.includeFacelet(parent, template);
		} catch (Throwable th) {
			throw new TagException(this.tag, "Error inserting template '"
					+ template + "': " + th.getMessage());
		} finally {
			ctx.setVariableMapper(orig);
		}
	}
	
	public boolean hasValue(FaceletContext ctx, String name) {
		TagAttribute tagAttribute = getAttribute(name);
		if (tagAttribute != null) {
			String value = tagAttribute.getValue(ctx);
			return !StringUtils.isBlank(value);
		}
		return false;
	}

	public static String appendExpression(String expression, String value) {
		StringBuffer sb = new StringBuffer(expression);
		int offset = sb.length() - 1;
		sb.insert(offset++, '.');
		sb.insert(offset, value);
		return sb.toString();
	}

	@Override
	protected MetaRuleset createMetaRuleset(Class type) {
		MetaRuleset set = super.createMetaRuleset(type);
		componentManager.updateMetaRuleset( this, set );
		return set;
	}

	@Override
	protected void setAttributes( FaceletContext ctx, Object instance ) {
		super.setAttributes(ctx, instance);
		componentManager.setAttributes( this, ctx, (UIComponent) instance );
	}
	
}
