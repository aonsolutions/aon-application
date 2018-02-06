package com.esferalia.aon.occam.api.model.management;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface OfferProperties {
	
	Property<Integer> getIdProperty();
	
	Property<Integer> getDomainProperty();
	
	Property<Byte> getStatusProperty();

	Property<Date> getStartIssueDateProperty();

	Property<Date> getEndIssueDateProperty();
	
	Property<Integer> getScopeProperty();

	Property<Byte> getConfidentialProperty();

	Property<Integer> getSellerProperty();
	
	Property<Integer> getWorkplaceProperty();
	
	Property<String> getSeriesProperty();
}
