package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface InvoiceProperties extends Serializable {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getActivityProperty();
	Property<Integer> getInvestAssetProperty();
	Property<Integer> getProjectProperty();
	Property<String> getSeriesProperty();
	Property<Integer> getNumberProperty();	
	Property<String> getReferenceCodeProperty();
	Property<Integer> getRegistryProperty();
	Property<String> getRegistryDocumentProperty();
	Property<Byte> getRegistryDocumentTypeProperty();
	Property<String> getRegistryDocumentCountryProperty();
	Property<String> getRegistryNameProperty();
	Property<Integer> getRegistryAddressProperty();
	Property<Date> getIssueDateProperty();
	Property<Date> getTaxDateProperty();
	Property<Byte> getConfidentialProperty();
	Property<Byte> getStatusProperty();
	Property<Byte> getTypeProperty();
	Property<Byte> getSurchargeProperty();
	Property<Byte> getWithholdingProperty();
	Property<Byte> getWithholdingFarmerProperty();
	Property<Byte> getVatAccrualPayment(); 
	Property<String> getCommentsProperty();
	Property<String> getRemarksProperty();
	Property<Byte> getInvestmentProperty();
	Property<Byte> getTransactionProperty(); 
	Property<Byte> getSignedProperty();
	Property<Integer> getScopeProperty(); 
	Property<Byte> getServiceProperty();
	Property<Byte> getRectificationTypeProperty();
	Property<Integer> getRectificationInvoiceProperty();
	Property<Byte> getAdvanceProperty();
	Property<Integer> getPosShiftProperty(); 
	Property<Integer> getSellerProperty(); 
	Property<Double> getTaxableBaseProperty();
	Property<Double> getVatQuotaProperty();
	Property<Double> getRetentionQuotaTotalProperty();
	Property<Double> getTotalProperty();
	Property<String> getCreationUserProperty();
	Property<Timestamp> getCreationDateProperty();
	Property<Timestamp> getModificationDateProperty();
	Property<String> getModificationUserProperty();

}
