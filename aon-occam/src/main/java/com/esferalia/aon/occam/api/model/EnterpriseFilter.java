package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface EnterpriseFilter{
	
	Filter filter(EnterpriseProperties properties);

}
