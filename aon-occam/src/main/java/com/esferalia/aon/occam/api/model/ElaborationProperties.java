package com.esferalia.aon.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AuditProperties;

public interface ElaborationProperties extends AuditProperties {

	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getSeriesProperty();
	Property<Integer> getNumberProperty();
	Property<Timestamp> getDateProperty();
	Property<Integer> getItemProperty();
	Property<String> getDescriptionProperty();
	Property<Integer> getWarehouseProperty();
	Property<Double> getQuantityProperty();
	Property<Byte> getStatusProperty();
	Property<String> getCommentsProperty();
	Property<String> getRemarksProperty();
	Property<Byte> getSourceProperty();
	Property<Integer> getSourceIdProperty();
	
}
