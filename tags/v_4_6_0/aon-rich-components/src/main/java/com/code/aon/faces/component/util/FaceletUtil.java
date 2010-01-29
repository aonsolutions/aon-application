package com.code.aon.faces.component.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.el.ELException;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.net.DummyHandler;
import com.code.aon.faces.component.richfaces.IRichFacesTags;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;

public class FaceletUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(FaceletUtil.class);
	
	public final static Class[] ACTION_SIG = new Class[0];

	public final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };
	
	public final static Class[] VALUE_CHANGE_LISTENER_SIG = new Class[] { ValueChangeEvent.class };
	
	public final static Class[] LOOKUP_CHANGE_LISTENER_SIG = new Class[] { LookupChangeEvent.class };
	
	public final static Class[] VALIDATOR_SIG = new Class[] { FacesContext.class, UIComponent.class, Object.class };
	
    public final static FaceletHandler LEAF_HANDLER = new FaceletHandler() {
        public void apply(FaceletContext ctx, UIComponent parent)
                throws IOException, FacesException, FaceletException,
                ELException {
        }
        public String toString() {
            return "FaceletHandler Aon Leaf";
        }
    };
	
	public static URL getTemplate(String resource) {
		ClassLoader loader = FaceletUtil.class.getClassLoader();
		URL url = loader.getResource(resource);
		try {
			url = new URL(null, url.toExternalForm(), new DummyHandler() );
		} catch (MalformedURLException e) {
			LOGGER.error( e.getMessage(), e );
		}
		return url;
	}

	public static TagAttribute getAttribute(Tag tag, String name) {
		return tag.getAttributes().get(name);
	}
	
	public static boolean hasValue(FaceletContext ctx, Tag tag, String name) {
		TagAttribute tagAttribute = getAttribute(tag, name);
		if (tagAttribute != null) {
			String value = tagAttribute.getValue(ctx);
			return !StringUtils.isBlank(value);
		}
		return false;
	}

	public static ValueExpression getMethodExpression(FaceletContext ctx, TagAttribute tag, Class type, Class[] paramTypes ) {
		ValueExpression valueExpression = null;
		if (tag != null) {
			ValueExpression ve = tag.getValueExpression(ctx, Object.class );
			MethodExpression methodExpression = tag.getMethodExpression( ctx, type, paramTypes );
			valueExpression = new MethodValueExpression( ve, methodExpression );
		}
		return valueExpression;
	}

	public static ValueExpression getMethodEmptyExpression(FaceletContext ctx, String name, Class type, Class[] paramTypes ) {
        ExpressionFactory f = ctx.getExpressionFactory();
        ValueExpression ve = f.createValueExpression( ctx, "", Object.class );
        MethodExpression me = f.createMethodExpression(ctx, name, type, paramTypes );
        return new MethodValueExpression( ve, me );
	}

	public static MethodExpression getMethodExpression(FaceletContext ctx, String name, Class type, Class[] paramTypes ) {
        return ctx.getExpressionFactory().createMethodExpression(ctx, name, type, paramTypes );
	}
	
	public static void insertTemplate(FaceletContext ctx, Tag tag, UIComponent parent, URL template, VariableMapper newMapper ) {
		VariableMapper orig = ctx.getVariableMapper();
		ctx.setVariableMapper(newMapper);
		try {
			ctx.includeFacelet(parent, template);
		} catch (Throwable th) {
			throw new TagException(tag, "Error inserting template '"
					+ template + "': " + th.getMessage());
		} finally {
			ctx.setVariableMapper(orig);
		}
	}
	
	public static ValueExpression getValueExpression(FaceletContext ctx, TagAttribute tag, String _default, Class _class ) {
		ValueExpression result = null;
		if ( tag != null ) {
			result = tag.getValueExpression(ctx, _class);
		} else {
			result = getValueExpression(ctx, _default, _class); 
		}
		return result;
	}
	
	public static ValueExpression getStringValueExpression(FaceletContext ctx, TagAttribute tag ) {
		return getValueExpression(ctx, tag, "", String.class);
	}
	
	public static ValueExpression getBooleanValueExpression(FaceletContext ctx, TagAttribute tag, boolean _default) {
		return getValueExpression(ctx, tag, (_default) ? "true" : "false", Boolean.class);
	}

	public static ValueExpression getBooleanValueExpression(FaceletContext ctx, TagAttribute tag) {
		return getBooleanValueExpression(ctx, tag, false);
	}

	public static ValueExpression getValueExpression(FaceletContext ctx, String expression, Class _class ) {
		return ctx.getExpressionFactory().createValueExpression(ctx, expression, _class);
	}
	
	public static String appendExpression(String expression, String value) {
		StringBuffer sb = new StringBuffer(expression);
		int offset = sb.length() - 1;
		sb.insert(offset++, '.');
		sb.insert(offset, value);
		return sb.toString();
	}

	public static String updateList(FaceletContext ctx, TagAttribute tag, String value ) {
		String result = value;
		if (tag != null) {
			String current = tag.getValue(ctx);
			if (! StringUtils.isBlank(current) ) {
				String[] ids = StringUtils.split(current, " ,");
				if (! ArrayUtils.contains(ids, value) ) {
					result = current + ", " + value;
				} else {
					result = current;
				}
			}
		}
		return result;
	}
	
	public static Object getProperty( FacesContext ctx, UIComponent c, String name ) {
        ValueBinding vb = c.getValueBinding(name);
        if ( vb == null ) {
        	return c.getAttributes().get(name);
        } 
        return vb.getValue(ctx);
	}
	
	public static boolean isRendered( FaceletContext ctx, Tag tag ) {
		boolean rendered = true;
		TagAttribute renderedTag = getAttribute(tag, IRichFacesTags.RENDERED);
		if ( renderedTag != null ) {
			rendered = renderedTag.getBoolean(ctx);
		}
		return rendered;
	}	
	
}
