package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.security.Scope;

public interface IScopable<T> {

	Scope getScope();
	T setScope(Scope scope);
	
}

