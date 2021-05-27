package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface BonusProperties{
	
	Property<Integer> getIdProperty();
	
	Property<Integer> getDomainProperty();

	Property<Boolean> getIsUnknowProperty();
	
}
