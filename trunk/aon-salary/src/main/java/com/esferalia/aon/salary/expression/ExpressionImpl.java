package com.esferalia.aon.salary.expression;

public class ExpressionImpl implements IExpression {
	
	private String name;
	private ExpressionScope scope;
	private String expression;

	@Override
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ExpressionScope getScope() {
		return scope;
	}
	public void setScope(ExpressionScope scope) {
		this.scope = scope;
	}

	@Override
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	@Override
	public boolean isReadOnly() {
		return getScope() == ExpressionScope.SYSTEM ||
			getScope() == ExpressionScope.SALARY;
	}
	

}
