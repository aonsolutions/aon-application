package com.code.aon.faces.component.util;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;

public class MethodValueExpression extends ValueExpression implements
		Externalizable {
	
	private ValueExpression orig;

	private MethodExpression methodExpression;

	public MethodValueExpression() {
	}

	public MethodValueExpression(ValueExpression orig,
			MethodExpression methodExpression) {
		this.orig = orig;
		this.methodExpression = methodExpression;
	}

	@Override
	public Class getExpectedType() {
		return orig.getExpectedType();
	}

	@Override
	public Class getType(ELContext ctx) {
		return MethodExpression.class;
	}

	@Override
	public Object getValue(ELContext ctx) {
		return methodExpression;
	}

	@Override
	public boolean isReadOnly(ELContext ctx) {
		return orig.isReadOnly(ctx);
	}

	@Override
	public void setValue(ELContext ctx, Object val) {
	}

	@Override
	public boolean equals(Object val) {
		return orig.equals(val);
	}

	@Override
	public String getExpressionString() {
		return orig.getExpressionString();
	}

	@Override
	public int hashCode() {
		return orig.hashCode();
	}

	@Override
	public boolean isLiteralText() {
		return orig.isLiteralText();
	}

	/**
	 * *
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		orig = (ValueExpression) in.readObject();
		methodExpression = (MethodExpression) in.readObject();
	}

	/**
	 * *
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(orig);
		out.writeObject(methodExpression);
	}
	
}