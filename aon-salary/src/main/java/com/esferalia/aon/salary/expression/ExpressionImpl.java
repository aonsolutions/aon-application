package com.esferalia.aon.salary.expression;

public class ExpressionImpl implements IExpression {
	
	private String name;
	private ExpressionScope scope;
	private String expression;

	@Override
	public String getName() {
		return name;
	}
	public ExpressionImpl setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public ExpressionScope getScope() {
		return scope;
	}
	public ExpressionImpl setScope(ExpressionScope scope) {
		this.scope = scope;
		return this;
	}

	@Override
	public String getExpression() {
		return expression;
	}
	public ExpressionImpl setExpression(String expression) {
		this.expression = expression;
		return this;
	}
	
	@Override
	public boolean isReadOnly() {
		return getScope() == ExpressionScope.SYSTEM ||
			getScope() == ExpressionScope.SALARY;
	}
	

}
