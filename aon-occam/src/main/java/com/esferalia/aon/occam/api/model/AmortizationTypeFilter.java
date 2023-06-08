package com.esferalia.aon.occam.api.model;

@FunctionalInterface
public interface AmortizationTypeFilter{
	
	Filter filter(AmortizationTypeProperties properties);

}
