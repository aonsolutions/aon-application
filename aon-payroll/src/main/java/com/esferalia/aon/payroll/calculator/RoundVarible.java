package com.esferalia.aon.payroll.calculator;

import java.math.BigDecimal;
import java.util.function.Function;

import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.expression.Period;

public class  RoundVarible implements ITimedVariable<BigDecimal> {

	private ITimedVariable<?> variable;
	private Function<Object, BigDecimal> f;
	
	public RoundVarible(ITimedVariable<?> variable, Function<Object, BigDecimal> f) {
		this.f = f;
		this.variable = variable;
	}

	@Override
	public Period getPeriod() {
		return variable.getPeriod();
	}

	@Override
	public BigDecimal getValue(Period period) {
		Object value = variable.getValue(period);
		if ( value == null ) 
			return null;
		
		return f.apply(value);
	}
	
	

}
