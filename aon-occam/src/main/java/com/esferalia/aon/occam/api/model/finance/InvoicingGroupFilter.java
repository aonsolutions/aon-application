package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.InvoicingGroupProperties;

@FunctionalInterface
public interface InvoicingGroupFilter{
	
	Filter filter(InvoicingGroupProperties properties);

}
