package com.esferalia.aon.occam.api.model;

public interface IScopable<T> {

	Integer getScope();
	T setScope(Integer scope);
	
}

