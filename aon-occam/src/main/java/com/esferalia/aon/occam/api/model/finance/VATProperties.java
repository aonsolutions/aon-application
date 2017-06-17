package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface VATProperties  {
	Property<Integer> getInvoiceIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getRegistryProperty();
	Property<Byte> getInvoiceTypeProperty();
	Property<Byte> getInvoiceTransactionProperty();
	Property<Integer> getActivityProperty();
	Property<Byte> getInvestmentProperty();
	Property<Byte> getServiceProperty();
	Property<Byte> getAccrualRegimeProperty();
	Property<Byte> getFarmerRegimeProperty();
	Property<Byte> getSurchargeProperty();
	Property<Double> getPercentProperty();
	Property<Double> getSurchargePercentProperty();

}
