package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface InvoiceProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getRegistryProperty();
	Property<Byte> getTypeProperty();
	Property<Date> getStartIssueDateProperty();
	Property<Date> getEndIssueDateProperty();
	Property<Integer> getScopeProperty();
	Property<Byte> getConfidentialProperty();
	Property<Byte> getRectificationTypeProperty();
	Property<Integer> getRectificationInvoiceProperty();
	Property<Integer> getWorkplaceProperty();
	Property<Integer> getSellerProperty();
	Property<Integer> getProductProperty();
	Property<Integer> getItemProperty();
	Property<Integer> getProductCategoryProperty();
	Property<String> getProductCodeProperty();
	Property<Byte> getProductTypeProperty();
	Property<Byte> getTransactionProperty();
	Property<Byte> getInvestmentProperty();
	Property<Integer> getPosShiftroperty();	
	Property<Date> getTaxDateProperty();

}
