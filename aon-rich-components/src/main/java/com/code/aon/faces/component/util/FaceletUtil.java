package com.code.aon.faces.component.util;

import java.net.URL;

import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagException;

public class FaceletUtil {

	public final static Class[] ACTION_SIG = new Class[0];

	public final static Class[] ACTION_LISTENER_SIG = new Class[] { ActionEvent.class };
	
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
			result = ctx.getExpressionFactory().createValueExpression(ctx, _default, _class);
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
	
}
