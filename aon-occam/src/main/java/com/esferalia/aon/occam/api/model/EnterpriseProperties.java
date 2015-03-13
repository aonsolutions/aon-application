package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface EnterpriseProperties {
	
	Property<Integer> getIdProperty();
	
	Property<Integer> getDomainProperty();

	Property<Integer> getParentDomainProperty();

	Property<String> getNameProperty();

	Property<String> getAliasProperty();

	Property<String> getDocumentProperty();

}
