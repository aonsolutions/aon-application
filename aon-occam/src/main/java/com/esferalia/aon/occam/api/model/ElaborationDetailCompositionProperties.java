package com.esferalia.aon.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface ElaborationDetailCompositionProperties {

	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getElaborationDetailProperty();
	Property<Integer> getItemProperty();
	Property<Double> getQuantityProperty();
	Property<Integer> getWarehouseProperty();
	Property<String> getAddInfoProperty();
	Property<String> getCreationUserProperty();
	Property<Timestamp> getCreationDateProperty();
	Property<String> getModificationUserProperty();
	Property<Timestamp> getModificationDateProperty();
	
}
