package com.esferalia.aon.occam.api.model;

@FunctionalInterface
public interface EnterpriseFilter{
	
	Filter filter(EnterpriseProperties properties);

}
