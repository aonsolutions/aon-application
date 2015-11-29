package com.esferalia.aon.salary.expression;

public class UndefinedVariable implements ITimedVariable<String> {


	private Period period;
	
	public UndefinedVariable(Period period) {
		this.period = period;
	}
	
	@Override
	public Period getPeriod() {
		return period;
	}

	@Override
	public String getValue(Period period) {
		return null;
	}


}
