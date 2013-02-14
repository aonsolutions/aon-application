package com.esferalia.aon.salary.expression;

public interface IExpression {
	String getName();
	String getExpression();
	ExpressionScope getScope();
	boolean isReadOnly();
}
