package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AuditProperties;

public interface ElaborationDetailCompositionProperties extends AuditProperties {

	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getElaborationDetailProperty();
	Property<Integer> getItemProperty();
	Property<Double> getQuantityProperty();
	Property<Integer> getWarehouseProperty();
	Property<String> getAddInfoProperty();
	
}
