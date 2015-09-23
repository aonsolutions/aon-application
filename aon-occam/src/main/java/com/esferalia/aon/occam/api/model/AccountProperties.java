package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AccountProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getCodeProperty();
	Property<String> getDescriptionProperty();
	Property<String> getAliasProperty();
	Property<Byte> getEntryEnabledProperty();
	Property<Byte> getLevelProperty();
	Property<Byte> getActiveProperty();
	Property<String> getCostCenterProperty();
}
