package com.esferalia.aon.salary.expression;

import java.util.Map;

public interface IExpressionVariable<V> extends ITimedVariable<V> {
	IExpression getExpression();
	Map<String, ITimedVariable<?>> getContext();
}
