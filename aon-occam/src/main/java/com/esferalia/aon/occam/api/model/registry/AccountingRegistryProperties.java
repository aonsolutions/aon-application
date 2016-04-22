package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AccountingRegistryProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getAccountCodeProperty();
	Property<String> getAccountDescriptionProperty();
	Property<String> getNameProperty();
	Property<String> getDocumentProperty();
	Property<String> getAliasProperty();
	Property<Byte> getSecurityLevelProperty();
	
}
