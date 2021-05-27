package com.esferalia.aon.occam.api.model.management;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface OfferFilter{
	
	Filter filter(OfferProperties properties);

}
