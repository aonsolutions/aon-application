package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface FBatchDetailProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getFBatchProperty();
	Property<Integer> getFinanceProperty();
	Property<Double> getAmountProperty();
	Property<Byte> getStatusProperty();

}
