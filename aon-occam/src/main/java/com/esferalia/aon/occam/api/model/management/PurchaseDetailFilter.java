package com.esferalia.aon.occam.api.model.management;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface PurchaseDetailFilter{
	
	Filter filter(PurchaseDetailProperties properties);

}
