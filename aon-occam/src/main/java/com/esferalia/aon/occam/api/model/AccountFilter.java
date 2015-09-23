package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface AccountFilter{
	
	Filter filter(AccountProperties properties);

}
