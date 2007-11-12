package com.code.aon.faces.component;

import java.net.URL;

import javax.el.VariableMapper;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.StringUtils;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

public abstract class AonComponentHandler extends ComponentHandler {

    protected static final String PARTIAL_SUBMIT = "partialSubmit";
    protected static final String FALSE = "false";

    /**
	 * The Constructor.
	 * 
	 * @param config
	 *            the config
	 */
	public AonComponentHandler(ComponentConfig config) {
		super(config);
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
	
	protected boolean hasValue(FaceletContext ctx, String name) {
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

}
