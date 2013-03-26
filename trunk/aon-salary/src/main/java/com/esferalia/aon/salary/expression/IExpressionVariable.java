package com.esferalia.aon.salary.expression;

public interface IExpressionVariable<V> extends ITimedVariable<V> {
	IExpression getExpression();
}
