package com.esferalia.aon.occam.api.model.management;

import java.sql.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface SalesProperties{
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<Integer> getProjectProperty();
	Property<Integer> getCustomerProperty();
	Property<String> getSeriesProperty();
	Property<Integer> getNumberProperty();
	Property<String> getPurchaseReferenceProperty();
	Property<Integer> getShippingAddressProperty();
	Property<Integer> getSellerProperty();
	Property<String> getDiscountExprProperty();
	Property<Date> getIssueDateProperty();
	Property<Integer> getPayMethodProperty();
	Property<Byte> getDocumentTypeProperty();
	Property<Byte> getSecurityLevelProperty();
	Property<Byte> getStatusProperty();
	Property<String> getCommentsProperty();
	Property<String> getRemarksProperty();
	Property<Integer> getWorkplaceProperty();
	Property<Integer> getScopeProperty();
	Property<Short> getNumberOfPymntsProperty();
	Property<Short> getDaysToFirstPymntProperty();
	Property<Short> getDaysBetweenPymntsProperty();
	Property<String> getPymntDaysProperty();
	Property<String> getBankAccountProperty();
	Property<String> getBankAliasProperty();
	Property<String> getBicProperty();		
	Property<Byte> getPurchaseGeneratedProperty();
	Property<Integer> getCarrierProperty();
	Property<String> getShippingAlternativeAddressProperty();
	Property<String> getShippingAlternativeAddress2Property();
	Property<String> getShippingAlternativeZipProperty();
	Property<String> getShippingAlternativeCityProperty();
	Property<String> getShippingAlternativePhoneProperty();
	Property<String> getShippingAlternativeRecipientProperty();
	Property<String> getShippingContactProperty();
	Property<Byte> getShippingPeriodProperty();
}