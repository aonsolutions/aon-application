package com.esferalia.aon.salary.expression;

import java.util.Map;

public class TimedResult<V> extends TimedObject<V> implements ITimedResult<V> , IConstantVariable{
	
	private Map<String, ITimedVariable<?>> context;
	
	public TimedResult(V value, Period period, Map<String, ITimedVariable<?>> context ) {
		super(value, period);
		this.context = context;
	}
	
	@Override
	public Map<String, ITimedVariable<?>> getContext() {
		return context;
	}
}
