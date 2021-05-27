package com.esferalia.aon.salary.expression;

import java.util.Map;

public interface ITimedResult<V> extends ITimedObject<V>, ITimedVariable<V>{
	
	Map<String,ITimedVariable<?>> getContext();
	
}
