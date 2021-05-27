package com.esferalia.aon.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AuditProperties;

public interface ElaborationDetailProperties extends AuditProperties {

	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getElaborationProperty();
	Property<Timestamp> getDateProperty();
	Property<Integer> getItemProperty();
	Property<Double> getQuantityProperty();
	Property<Integer> getWarehouseProperty();
	Property<String> getAddInfoProperty();
	
}
