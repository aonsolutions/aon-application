package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface CreditorFilter{
	
	Filter filter(CreditorProperties properties);

}
