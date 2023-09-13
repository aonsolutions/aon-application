package com.esferalia.aon.salary.expression;

public interface IWrapTimedVariable<V> extends ITimedVariable<V> {
	ITimedVariable<V> getVariable();
}
