package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.Properties.VATProperties;


@Deprecated
public class Filters {
	
	private Filters() {
	}
	
	@FunctionalInterface
	public static interface VATFilter{
		Filter filter(VATProperties properties);
	}
	
	
}
