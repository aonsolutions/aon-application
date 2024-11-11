package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface FinanceProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Byte> getPaymentProperty();
	Property<Integer> getRegistryProperty();
	Property<Date> getDueDateProperty();
	Property<Integer> getInvoiceProperty();
	Property<Double> getAmountProperty();
	Property<String> getConceptProperty();
	Property<Byte> getStatusProperty();
	Property<Byte> getConfidentialProperty();
	Property<String> getInvoiceReferenceCodeProperty();
	Property<Date> getInvoiceDateProperty();
	Property<Integer> getPayMethodProperty();
	Property<Byte> getPayMethodTypeProperty();
	Property<Byte> getPayrollProperty();
	
	Property<String> getRegistryNameProperty();
	Property<String> getConceptNameProperty();

}
