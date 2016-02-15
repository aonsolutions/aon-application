package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface FinanceProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getRegistryProperty();
	Property<Date> getDueDateProperty();
	Property<Integer> getInvoiceProperty();
	Property<Byte> getStatusProperty();
	Property<Byte> getConfidentialProperty();

}
