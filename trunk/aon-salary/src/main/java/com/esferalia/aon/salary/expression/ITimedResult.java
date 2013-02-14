package com.esferalia.aon.salary.expression;

import java.util.Map;

public interface ITimedResult<V> extends ITimedObject<V> {
	
	Map<String,Object> getContext();
	
}
