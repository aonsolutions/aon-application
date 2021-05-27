package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.Properties.IRPFProperties;
import com.esferalia.aon.occam.api.model.finance.Properties.VATProperties;


public class Filters {
	
	@FunctionalInterface
	public static interface VATFilter{
		
		Filter filter(VATProperties properties);

	}
	
	@FunctionalInterface
	public static interface IRPFFilter{
		
		Filter filter(IRPFProperties properties);

	}
}
