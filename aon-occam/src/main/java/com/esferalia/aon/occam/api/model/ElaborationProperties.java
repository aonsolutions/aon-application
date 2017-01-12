package com.esferalia.aon.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface ElaborationProperties {

	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getSeriesProperty();
	Property<Integer> getNumberProperty();
	Property<Timestamp> getDateProperty();
	Property<Integer> getItemProperty();
	Property<Integer> getWarehouseProperty();
	Property<Double> getQuantityProperty();
	Property<Byte> getStatusProperty();
	Property<String> getCommentsProperty();
	Property<Byte> getSourceProperty();
	Property<Integer> getSourceIdProperty();
	Property<String> getCreationUserProperty();
	Property<Timestamp> getCreationDateProperty();
	Property<String> getModificationUserProperty();
	Property<Timestamp> getModificationDateProperty();
	
	
}
