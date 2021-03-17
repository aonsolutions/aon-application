package com.esferalia.aon.occam.api.model;

@FunctionalInterface
public interface AccountFilter{
	
	Filter filter(AccountProperties properties);

}
