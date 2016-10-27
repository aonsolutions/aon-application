package com.esferalia.aon.occam.api.model.management;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface PurchaseDetailProperties{
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getPurchaseProperty();
	Property<Integer> getProjectProperty();
	Property<Integer> getItemProperty();
	Property<Short> getLineProperty();
	Property<String> getDescriptionProperty();
	Property<Double> getQuantityProperty();
	Property<Double> getPriceProperty();
	Property<String> getDiscountExpressionProperty();
	Property<Double> getTaxesProperty();
	Property<Byte> getStatusProperty();
	Property<Integer> getProposalDetailProperty();
	Property<Byte> getSourceProperty();
	Property<Integer> getSourceIdProperty();
	Property<Double> getDeliveredProperty();
	
}