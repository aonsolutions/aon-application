package com.esferalia.aon.occam.api.model.management;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface SalesDetailProperties{
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getSalesProperty();
	Property<Integer> getItemProperty();
	Property<Short> getLineProperty();
	Property<String> getDescriptionProperty();
	Property<Double> getQuantityProperty();
	Property<Double> getPriceProperty();
	Property<String> getdiscountExpressionProperty();
	Property<Double> getTaxesProperty();
	Property<Byte> getStatusProperty();
	Property<Integer> getOfferDetailProperty();
	Property<Double> getDeliveredProperty();
	
}