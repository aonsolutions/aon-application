package com.esferalia.aon.salary.expression;

import java.util.Collections;
import java.util.Map;

public class ExpressionVariable<V> extends TimedObject<V> implements
		IExpressionVariable<V> {

	private static final Map<String, ITimedVariable<?>> EMPTY_CONTEXT = Collections
			.emptyMap();

	private IExpression expression;
	private Map<String, ITimedVariable<?>> context;

	public ExpressionVariable(V value, Period p, IExpression expression) {
		this(value, p, expression, EMPTY_CONTEXT);
	}

	public ExpressionVariable(V value, Period p, IExpression expression,
			Map<String, ITimedVariable<?>> context) {
		super(value, p);
		this.context = context;
		this.expression = expression;
	}

	@Override
	public IExpression getExpression() {
		return expression;
	}

	@Override
	public Map<String, ITimedVariable<?>> getContext() {
		return context;
	}

}
