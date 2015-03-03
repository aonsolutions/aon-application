package com.esferalia.aon.occam.api.model;


@FunctionalInterface
public interface SalaryFilter{
	
	Filter filter(SalaryProperties properties);

}
