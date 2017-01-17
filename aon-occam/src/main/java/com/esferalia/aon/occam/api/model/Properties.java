package com.esferalia.aon.occam.api.model;

import java.sql.Date;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface Properties {

	public interface SeriesProperties {
		Property<Integer> getIdProperty(); 
		Property<Integer> getDomainProperty();	
		Property<Integer> getScopeProperty();	
		Property<String> getDescriptionProperty();
		Property<Byte> getActiveProperty();
		Property<String> getCodeProperty();
		Property<Byte> getTasProperty();
		Property<Byte> getOfferProperty();
		Property<Byte> getSalesProperty();
		Property<Byte> getDeliveryProperty();
		Property<Byte> getInvoiceProperty();
		Property<Byte> getRectificationProperty();
		Property<Byte> getPosProperty();
		Property<Byte> getConfidentialProperty();
	}

	public interface WorkplaceProperties {
		
		Property<Integer> getIdProperty();
		Property<Byte> getActiveProperty();
		Property<Integer> getAddressProperty();
		Property<Integer> getCustomerProperty();
		Property<String> getDescriptionProperty();
		Property<Integer> getDomainProperty();
		Property<Byte> getEconomicagreementProperty();
		Property<Integer> getEnterpriseProperty();
		Property<Integer> getScopeProperty();
	}
	
	public interface ProjectProperties {
		
		Property<Integer> getIdProperty();
		Property<Byte> getActiveProperty();
		Property<String> getAliasProperty();
		Property<Byte> getCommercialProperty();
		Property<Date> getDateProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Integer> getProjectTypeProperty();
		Property<Integer> getRegistryProperty();
		Property<Byte> getReservationProperty();
		Property<Byte> getTasProperty();
	}
	
	public interface AttachProperties {

		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getDescriptionProperty();
		Property<Byte> getTypeProperty();
		Property<Date> getAttachDateProperty();
		Property<Timestamp> getAttachDateTimeStampProperty();
		Property<Integer> getCategoryProperty();
		Property<Date> getAttachCreationDateProperty();
		Property<Timestamp> getCreationDateTimeStampProperty();
		Property<String> getCreationUserProperty();
		Property<byte[]> getDataProperty();
		Property<String> getDparentIdProperty();
		Property<String> getDriveIdProperty();
		Property<Byte> getMimeTypeProperty();
		Property<Date> getAttachModificationDateProperty();
		Property<Timestamp> getModificationDateTimeStampProperty();
		Property<String> getModificationUserProperty();
		Property<Integer> getAttachModuleProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Integer> getSourceBatchProperty();
		Property<Byte> getSourceTypeProperty();
	}
	
	public interface InvoicingGroupProperties {
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Integer> getCustomerProperty();
		Property<Byte> getCustomerGroupedProperty();
		Property<String> getDescriptionProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getIdProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
	public interface CommercialTrackingProperties {
		Property<Integer> getActivityProperty();
		Property<Byte> getAlldayProperty();
		Property<String> getCommentsProperty();
		Property<Timestamp> getDateProperty();
		Property<Integer> getDomainProperty();
		Property<Timestamp> getEndDateProperty();
		Property<String> getEventIdProperty();
		Property<Integer> getIdProperty();
		Property<String> getLocationProperty();
		Property<Integer> getNextCommercialTrackingProperty();
		Property<Integer> getOfferProperty();
		Property<Integer> getProjectCommercialProperty();
		Property<Integer> getSellerProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface CommercialActivityProperties {
		Property<Integer> getDomainProperty();
		Property<Integer> getIdProperty();
		Property<String> getNameProperty();
		Property<Integer> getProbabilityProperty();
		Property<Integer> getSurveyProperty();
	}
	
	public interface DomainGserviceaccountProperties {
		Property<String> getClientIdProperty();
		Property<byte[]> getClientSecretProperty();
		Property<Integer> getDomainProperty();
		Property<String> getEmailAddressProperty();
		Property<String> getGoogleAccountProperty();
		Property<Double> getLimitProperty();
		Property<byte[]> getPrivateKeyProperty();
		Property<String> getPublicKeyProperty();
		Property<Double> getSizeProperty();			
	}
	
	public interface WarehouseProperties {
		Property<Byte> getActiveProperty();
		Property<Integer> getDepartmentProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getIdProperty();
		Property<String> getNameProperty();
		Property<Integer> getWorkplaceProperty();			
	}
	
	public interface FeeProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProjectProperty();
		Property<Integer> getCustomerProperty();
		Property<Short> getLineProperty();
		Property<Integer> getItemProperty();
		Property<String> getDescriptionProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getPriceProperty();
		Property<String> getDiscountExprProperty();
		Property<Date> getInitialDateProperty();
		Property<Date> getFinalDateProperty();
		Property<Date> getBillingDateProperty();
		Property<Short> getPeriodProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Integer> getInvoicingGroupProperty();
		Property<Integer> getSellerProperty();
		Property<Integer> getWorkplaceProperty();
	}
	
	public interface DepartmentProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
	}
	
	public interface ProductProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<String> getCodeProperty();
		Property<Byte> getKindProperty();
		Property<Integer> getBrandProperty();
		Property<Integer> getCategoryProperty();
		Property<Byte> getInventoriableProperty();
		Property<Byte> getSerializableProperty();
		Property<Byte> getLotableProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getVatProperty();
		Property<Integer> getRetentionProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getManufacturedProperty();
		Property<Byte> getCompositionProperty();
		Property<Byte> getCompositionPriceProperty();
		Property<Integer> getSalesAccountProperty();
		Property<Integer> getPurchaseAccountProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	
	public interface ItemProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProductProperty();
		Property<String> getDetailProperty();
		Property<String> getDetail2Property();
		Property<String> getDetail3Property();
		Property<String> getDescriptionProperty();
		Property<String> getSerialNumberProperty();
		Property<Date> getSerialDateProperty();
		Property<Double> getPriceProperty();
		Property<Byte> getStatusProperty();
		Property<Double> getExpensesPercentProperty();
		Property<Double> getExpensesFixedProperty();
		Property<Double> getProfitPercentProperty();
		Property<Double> getPurchasePriceProperty();
		Property<Byte> getInternetProperty();
		Property<String> getBarcodeProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<Integer> getPackFormatTagProperty();
		Property<Integer> getPackUnitsProperty();
		Property<Integer> getPackUnitsTagProperty();
		Property<Double> getPackMeasurementProperty();
		Property<Integer> getPackMeasurementTagProperty();
		Property<Integer> getStockUnitTagProperty();
	}
	
	public interface DomainProperties {
		Property<Byte> getActiveProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<String> getDescriptionProperty();
		Property<Byte> getDisabledomainmanagementProperty();
		Property<Byte> getDomainmanagementProperty();
		Property<Byte> getEnableheredityProperty();
		Property<Date> getExpirationdateProperty();
		Property<Integer> getIdProperty();
		Property<Timestamp> getLastaccessDateProperty();
		Property<String> getLastaccessUserProperty();
		Property<Integer> getMaxdefinedusersProperty();
		Property<Integer> getMaxdocumentsizeProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
		Property<String> getNameProperty();
		Property<String> getOwnerProperty();
		Property<Integer> getParentProperty();
		Property<Integer> getScopeProperty();
		Property<String> getSubdomainsuffixProperty();
		Property<Byte> getTypeProperty();
	}

	public interface MailAccountProperties{
		Property<Integer> getIdProperty();
		Property<String> getNameProperty();
		Property<String> getEmailProperty();
		Property<Integer> getSignatureProperty();
		Property<Byte> getDefaultAccountProperty();
		Property<String> getDisplayNameProperty();
		Property<Integer> getDomainProperty();
		Property<String> getDraftFolderProperty();
		Property<String> getIncomingHostProperty();
		Property<Integer> getIncomingPortProperty();
		Property<Byte> getIncomingSecurityProperty();
		Property<String> getMailUsernameProperty();
		Property<String> getOutgoingHostProperty();
		Property<Integer> getOutgoingPortProperty();
		Property<Byte> getOutgoingSecurityProperty();
		Property<Byte> getOutgoingVerificationProperty();
		Property<String> getPasswordProperty();
		Property<String> getProtocolProperty();
		Property<String> getReplytoMailProperty();
		Property<String> getSentFolderProperty();
		Property<String> getSpamFolderProperty();
		Property<String> getTrashFolderProperty();
		Property<Byte> getTypeProperty();
		Property<Integer> getUserIdProperty();
	}
	
	public interface ContactProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getUserIdProperty();
		Property<String> getDisplayNameProperty();
		Property<Integer> getContactDataProperty();
	}
	
	public interface DeliveryProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProjectProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();
		Property<Integer> getCustomerProperty();
		Property<Integer> getAddressProperty();
		Property<Timestamp> getIssueTimeProperty();
		Property<Integer> getPayMethodProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Byte> getStatusProperty();
		Property<String> getCommentsProperty();
		Property<String> getRemarksProperty();
		Property<Integer> getWorkplaceProperty();
		Property<Integer> getScopeProperty();
		Property<Short> getNumberOfPymntsProperty();
		Property<Short> getDaysToFirstPymntProperty();
		Property<Short> getDaysBetweenPymntProperty();
		Property<String> getPymntDaysProperty();
		Property<String> getBankAccountProperty();
		Property<String> getBankAliasProperty();
		Property<String> getBicProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
	public interface DeliveryDetailProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getDelivery();
		Property<Short> getLine();
		Property<Integer> getItem();
		Property<String> getDescriptionProperty();
		Property<Integer> getWarehouse();
		Property<Double> getQuantity();
		Property<Double> getPrice();
		Property<String> getDiscountExpressionProperty();
		Property<Integer> getSalesDetail();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
	public interface TagProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getColorProperty();
		Property<String> getNameProperty();
		Property<Byte> getTypeProperty();
	}
	
	public interface BrandProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
	}
	
	public interface TaxProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Double> getPercentageProperty();
		Property<Integer> getPurchaseAccountProperty();
		Property<Integer> getSalesAccountProperty();
		Property<Double> getSurchargeProperty();
		Property<Byte> getTaxTypeProperty();
		Property<Byte> getVatDeductionTypeProperty();
		Property<Byte> getWithholdingTypeProperty();
		
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
	public interface ProductCategoryProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<String> getDetailProperty();
		Property<String> getDetail2Property();
		Property<String> getDetail3Property();
	}
	
	public interface SignatureProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<String> getSignatureProperty();
		Property<Integer> getUserIdProperty();
	}
	
	public interface RegistryAddressProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Byte> getTypeProperty();	
		Property<String> getRecipientProperty();
		Property<String> getStreetTypeProperty();
		Property<String> getAddressProperty();
		Property<String> getAddress2Property();
		Property<String> getAddress3Property();
		Property<String> getNumberProperty();
		Property<String> getZipProperty();
		Property<String> getCityProperty();
		Property<Integer> getGeozoneProperty();
		Property<String> getAliasProperty();
		Property<String> getMunicipalityCodeProperty();
	}
	
	public interface RegistryMediaProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Byte> getMediaProperty();
		Property<String> getValueProperty();
		Property<String> getCommentProperty();
		Property<Byte> getAdministrativeProperty();
		Property<Byte> getCommercialProperty();
		Property<Byte> getTechnicalProperty();
		Property<Integer> getRaddressProperty();
	}
	
	public interface RegistryNoteProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getDescriptionProperty();
		Property<Date> getNoteDateProperty();
		Property<String> getCommentsProperty();
		Property<Byte> getNoteTypeProperty();
		Property<Byte> getSecurityLevelProperty();
	}
	
	public interface StockProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getItemProperty();
		Property<Double> getQuantityProperty();
		Property<Integer> getWarehouseProperty();
	}
	
	public interface WarehouseTransferProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCommentsProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Integer> getInventoryProperty();
		Property<Timestamp> getIssueTimeProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Integer> getNumberProperty();
		Property<String> getSeriesProperty();
		Property<Byte> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<Integer> getSourceWarehouseProperty();
		Property<Integer> getTargetWarehouseProperty();
	}
	
	public interface WarehouseTransferDetailProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getWarehouseTransferProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Integer> getItemProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Double> getQuantityProperty();
	}
	
	public interface TaskProperties{
		Property<Integer> getActivityTypeProperty();
		Property<String> getCommentsProperty();
		Property<String> getDescriptionProperty();
		Property<Integer> getDomainProperty();
		Property<Timestamp> getDueDateProperty();
		Property<Timestamp> getEndDateProperty();
		Property<String> getGtaskIdProperty();
		Property<String> getGtasklisIdProperty();
		Property<Integer> getIdProperty();
		Property<Byte> getPercentProperty();
		Property<Byte> getPriorityProperty();
		Property<Integer> getProjectProperty();
		Property<Integer> getRegistryProperty();
		Property<Byte> getRepeatPeriodProperty();
		Property<Integer> getSenderProperty();
		Property<Byte> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<Timestamp> getStartDateProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getTaskHolderProperty();
		Property<Integer> getWorkgroupProperty();
		Property<Integer> getNumberProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<Integer> getParentProperty();
	}
	
	public interface TaskTagProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTagProperty();
		Property<Integer> getTaskProperty();
	}
	
	public interface TaskCommentProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskProperty();
		Property<String> getCommentProperty();
		Property<Integer> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();	
	}
	
	public interface TaskEventProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskProperty();
		Property<String> getEventProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	
	public interface TaskHolderWorkgroupProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskHolderProperty();
		Property<Integer> getWorkgroupProperty();
	}
	
	public interface TaskHolderProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getActiveProperty();
		Property<Integer> getUserIdProperty();
		Property<Integer> getCostProfileProperty();
	}

	public interface GeoZoneProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCodeProperty();
		Property<String> getNameProperty();
		Property<Byte> getSystemProperty();
	}
	
	public interface ProjectReservationProperties{
		Property<Integer> getProjectProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getHotelProperty();
		Property<Integer> getHotelReservationProperty();
		Property<String> getCodeProperty();
		Property<Date> getStartDateProperty();
		Property<Timestamp> getStartTimeProperty();
		Property<Date> getEndDateProperty();
		Property<Timestamp> getEndTimeProperty();
		Property<Integer> getSellerProperty();
		Property<Integer> getAgencyProperty();
		Property<Double> getAgencyCommissionPercentProperty();
		Property<Double> getAgencyCommissionAmountProperty();
		Property<Byte> getAgencyRebateProperty();
		Property<Integer> getCompanyProperty();
		Property<Double> getDiscountPercentProperty();
		Property<Double> getDiscountAmountProperty();
		Property<Byte> getBookingHolderProperty();
		Property<Double> getTaxableBaseProperty();
		Property<Double> getVatQuotaProperty();
		Property<Double> getOtherTaxQuotaProperty();
		Property<Double> getTotalProperty();
		Property<String> getCommentsProperty();
		Property<String> getRemarksProperty();
		Property<Byte> getSourceProperty();
		Property<String> getCrsCodeProperty();
		Property<Double> getAdvanceProperty();
		Property<Byte> getAdvanceInvoicedProperty();
		Property<Byte> getEarlyCheckOutProperty();
		Property<Byte> getPrepayProperty();
		Property<String> getBankTransactionProperty();
		Property<String> getCreditCardHolderProperty();
		Property<String> getCreditCardNumberProperty();
		Property<String> getCreditCardExpirationMonthProperty();
		Property<String> getCreditCardExpirationYearProperty();
		Property<String> getCreditCardCvvProperty();
		Property<String> getCreditPenaltyValueProperty();	
		Property<Byte> getTouristTaxFreeProperty();
		Property<Byte> getCheckStatusProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getCancellationUserProperty();
		Property<Timestamp> getCancellationDateProperty();
		Property<String> getTokenProperty();
		Property<Double> getPenaltyAmountProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface ProjectCommercialProperties{
		Property<Integer> getProjectProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTargetProperty();
		Property<Integer> getSellerProperty();
		Property<String> getCommentsProperty();
		Property<Byte> getSourceProperty();
		Property<Byte> getStatusProperty();
		Property<Date> getStatusDateProperty();
	}


}
