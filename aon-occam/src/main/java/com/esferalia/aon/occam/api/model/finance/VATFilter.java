package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface VATFilter{
	
	Filter filter(VATProperties properties);

}
