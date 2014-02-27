package com.code.aon.faces.component.util;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodInfo;
import javax.faces.component.UIComponent;

import org.apache.commons.lang.ArrayUtils;

public class MethodExpressionAdapter extends MethodExpression {

	private static final long serialVersionUID = 1L;
	
	private MethodExpression source;
	private MethodExpression adapted;
	private UIComponent component;
	
	public MethodExpressionAdapter() {
		super();
	}	
	
	public MethodExpressionAdapter(MethodExpression source,
			MethodExpression adapted, UIComponent component) {
		this.source = source;
		this.adapted = adapted;
		this.component = component;
	}

	@Override
	public MethodInfo getMethodInfo(ELContext context) {
		return getMethodInfo(context);
	}

	@Override
	public Object invoke(ELContext context, Object[] params) {
		Object[] newParams = ArrayUtils.add(params, component);
		return adapted.invoke(context, newParams);
	}

	@Override
	public String getExpressionString() {
		return source.getExpressionString();
	}

	@Override
	public boolean equals(Object obj) {
		return source.equals(obj);
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public boolean isLiteralText() {
		return source.isLiteralText();
	}

}
