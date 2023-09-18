package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AmortizationTypeProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	
	Property<String> getDescriptionProperty();
	Property<Double> getPercentageProperty();
	
	Property<String> getFixedAssetAccountProperty();
	Property<String> getAccumulatedAccountProperty();
	Property<String> getAllocationAccountProperty();
}
