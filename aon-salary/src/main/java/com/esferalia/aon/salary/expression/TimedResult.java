package com.esferalia.aon.salary.expression;

import java.util.Map;

public class TimedResult<V> extends TimedObject<V> implements ITimedResult<V> {
	
	private Map<String, Object> context;
	
	public TimedResult(V value, Period period, Map<String, Object> context ) {
		super(value, period);
		this.context = context;
	}
	
	@Override
	public Map<String, Object> getContext() {
		return context;
	}
}
