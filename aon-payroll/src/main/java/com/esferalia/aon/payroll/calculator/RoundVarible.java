package com.esferalia.aon.payroll.calculator;

import java.util.function.UnaryOperator;

import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;

public class  RoundVarible implements ITimedVariable<Double> {

	private UnaryOperator<Double> f;
	private ITimedVariable<?> variable;
	
	public RoundVarible(ITimedVariable<?> variable, UnaryOperator<Double> f) {
		this.f = f;
		this.variable = variable;
	}

	@Override
	public Period getPeriod() {
		return variable.getPeriod();
	}

	@Override
	public Double getValue(Period period) {
		Object value = variable.getValue(period);
		if ( value == null ) 
			return null;
		
		return f.apply(((Number)value).doubleValue());
	}
	
	

}
