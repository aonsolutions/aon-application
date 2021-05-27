package com.esferalia.aon.salary.expression;

public interface ITimedVariable<V> {

	public Period getPeriod();
	public V getValue(Period period);

}
