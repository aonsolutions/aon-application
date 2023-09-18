package com.code.aon.faces.component.util;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.MethodInfo;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;

public class MethodExpressionAdapter extends MethodExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private MethodExpression source;
	private MethodExpression adapted;
	private Object[] properties;
	
	public MethodExpressionAdapter() {
		super();
	}	
	
	public MethodExpressionAdapter(MethodExpression source,
			MethodExpression adapted, Object[] properties) {
		this.source = source;
		this.adapted = adapted;
		this.properties = properties;
	}

	@Override
	public MethodInfo getMethodInfo(ELContext context) {
		return getMethodInfo(context);
	}

	@Override
	public Object invoke(ELContext context, Object[] params) {
		Object[] newParams = ArrayUtils.add(params, properties);
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
