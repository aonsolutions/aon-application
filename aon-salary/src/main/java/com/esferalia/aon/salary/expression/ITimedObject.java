package com.esferalia.aon.salary.expression;

public interface ITimedObject<V> {
	
	public V getValue();
	public Period getPeriod();
}
