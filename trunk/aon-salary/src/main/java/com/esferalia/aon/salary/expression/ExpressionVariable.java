package com.esferalia.aon.salary.expression;

public class ExpressionVariable<V> extends TimedObject<V> implements IExpressionVariable<V>{

	private IExpression expression;
	
	public ExpressionVariable(V value, Period p, IExpression expression) {
		super(value, p );
		this.expression = expression;
	}
	
	@Override
	public IExpression getExpression() {
		return expression;
	}
	
}
