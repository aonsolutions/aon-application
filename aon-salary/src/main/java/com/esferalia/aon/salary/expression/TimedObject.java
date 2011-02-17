package com.esferalia.aon.salary.expression;

import java.util.Date;
import java.util.List;

public class TimedObject<V> implements ITimedObject<V> {

	private V value;
	private Period period;
	
	public TimedObject(V value, Period period ) {
		this.value = value;
		this.period = period;
	}
	
	public TimedObject(V value, Date start, Date end ) {
		this ( value, new Period(start, end) );
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public Period getPeriod() {
		return period;
	}
	
	

}
