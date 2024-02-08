package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface InvoiceProperties extends Serializable {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getSeriesProperty();
	Property<Byte> getStatusProperty();
	Property<Integer> getNumberProperty();	
	Property<String> getReferenceCodeProperty();
	Property<Integer> getActivityProperty();
	Property<Integer> getRegistryProperty();
	Property<String> getRegistryDocumentProperty();
	Property<String> getRegistryNameProperty();
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
	Property<Integer> getProductBrandProperty();
	Property<String> getProductCodeProperty();
	Property<Byte> getProductTypeProperty();
	Property<Byte> getTransactionProperty();
	Property<Byte> getInvestmentProperty();
	Property<Integer> getPosShiftroperty();	
	Property<Date> getTaxDateProperty();
	Property<Byte> getVatAccrualPayment();

	Property<Timestamp> getCreationDateProperty();
	Property<String> getCreationUserProperty();
	Property<Timestamp> getModificationDateProperty();
	Property<String> getModificationUserProperty();
	
	// INVOICE COMMUNICATION
	Property<Byte> getInvoiceInfoTypeProperty();
	Property<Byte> getInvoiceInfoStatusProperty();
	
	// RSELLER DATES (FOR GLOBAL STATICS)
	Property<Integer> getRSellerIdProperty();
	Property<java.sql.Date> getRSellerStartDateProperty();
	Property<java.sql.Date> getRSellerEndDateProperty();
	Property<Byte> getRSellerStatusProperty();
}
