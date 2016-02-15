package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface CreditorProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getNameProperty();
	Property<String> getDocumentProperty();
	Property<String> getAliasProperty();
	Property<Byte> getActiveProperty();
	Property<Byte> getSecurityLevelProperty();
	
}
