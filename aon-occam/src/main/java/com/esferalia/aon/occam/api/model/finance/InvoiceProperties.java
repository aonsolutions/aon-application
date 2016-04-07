package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface InvoiceProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Byte> getTypeProperty();
	Property<Date> getStartIssueDateProperty();
	Property<Date> getEndIssueDateProperty();
	Property<Integer> getScopeProperty();
	Property<Byte> getConfidentialProperty();
	Property<Integer> getWorkplaceProperty();
	Property<Integer> getSellerProperty();
	Property<Integer> getProductProperty();
	Property<Integer> getItemProperty();
	Property<Integer> getProductCategoryProperty();

}
