package com.esferalia.aon.occam.api.model;

import java.sql.Date;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface Properties {
	
	public interface AuditProperties {
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	
	public interface ApplicationParameterProperties {
		Property<Integer> getIdProperty(); 
		Property<Integer> getDomainProperty();	
		Property<String> getNameProperty();
		Property<String> getValueProperty();
	}
	
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
		Property<Integer> getTagProperty();
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
	
	public interface InvoicingGroupProperties extends AuditProperties{
		Property<Integer> getCustomerProperty();
		Property<Byte> getCustomerGroupedProperty();
		Property<String> getDescriptionProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getIdProperty();
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
		
		Property<Integer> getCategoryProperty();
	}
	
	public interface DepartmentProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
	}
	
	public interface ProductProperties extends AuditProperties {
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
	}
	
	public interface ItemProperties extends AuditProperties{
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
		Property<Integer> getPackFormatTagProperty();
		Property<Integer> getPackUnitsProperty();
		Property<Integer> getPackUnitsTagProperty();
		Property<Double> getPackMeasurementProperty();
		Property<Integer> getPackMeasurementTagProperty();
		Property<Integer> getStockUnitTagProperty();
	}
	
	public interface ItemAddInfoProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProductProperty();
		Property<Integer> getItemProperty();
		Property<String> getAttributeProperty();
		Property<String> getValueProperty();
		Property<Date> getValueDate();
	}
	
	public interface RegistryAddInfoProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getAttributeProperty();
		Property<String> getValueProperty();
		Property<Date> getValueDate();
	}
	
	public interface RDirStaffProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Byte> getShareHolderProperty();
		Property<Byte> getRepresentativeProperty();
		Property<Byte> getRepresentativeLaborProperty();
		Property<Byte> getDirectorProperty();
	}
	
	public interface DomainProperties extends AuditProperties{
		Property<Byte> getActiveProperty();
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
		Property<String> getNameProperty();
		Property<String> getOwnerProperty();
		Property<Integer> getParentProperty();
		Property<Integer> getScopeProperty();
		Property<String> getSubdomainsuffixProperty();
		Property<Byte> getTypeProperty();
	}
	
	public interface DomainAppProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Byte> getAppProperty();
		Property<Byte> getActiveProperty();
	}
	
	public interface ScopeProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getDescriptionProperty();
	}
	
	public interface UserAppRoleProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getUserIdProperty();
		Property<Byte> getAppProperty();
		Property<Byte> getRoleProperty();
	}
	
	public interface UserScopeProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getUserIdProperty();
		Property<Integer> getScopeProperty();
	}
	

	public interface UserWorkgroupProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getUserIdProperty();
		Property<Integer> getWorkgroupProperty();
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
	
	public interface DeliveryProperties extends AuditProperties{
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
		Property<Integer> getCarrierProperty();
		Property<Integer> getCarrierPackingProperty();
		// REGISTRY
		Property<String> getRegistryNameProperty();
		Property<String> getRegistryDocumentProperty();
		
		
		Property<Byte> getConfidentialProperty();
	}
	
	public interface DeliveryDetailProperties extends AuditProperties{
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
	
	public interface TaxProperties extends AuditProperties{
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
	
	public interface RegistrySellerProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Integer> getSellerProperty();
		Property<Date> getStartDateProperty();
		Property<Date> getEndDateProperty();
		Property<Byte> getStatusProperty();
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
	
	public interface RegistryItemProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Integer> getItemProperty();
		Property<Byte> getTypeProperty();
		Property<String> getCodeProperty();
		Property<Double> getPriceProperty();
		Property<String> getDiscountExprProperty();
		Property<Byte> getPriorityProperty();
		Property<Integer> getWorkplaceProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface RegistryBankProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getBankAccountProperty();
		Property<String> getBicProperty();
		Property<String> getSufixProperty();
		Property<String> getAliasProperty();
		Property<Byte> getActiveProperty();
		Property<Integer> getAccountProperty();
	}
	
	public interface RegistryPayMethodProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Integer> getPayMethodProperty();
		Property<Integer> getRBankProperty();
		Property<Integer> getNumberOfPymntsProperty();
		Property<Integer> getDaysToFirstPymntProperty();
		Property<Integer> getDaysBetweenPymntsProperty();
		Property<String> getPymntDaysProperty();
	}
	
	public interface StockProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getItemProperty();
		Property<Double> getQuantityProperty();
		Property<Integer> getWarehouseProperty();
	}
	
	public interface WarehouseTransferProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCommentsProperty();
		Property<Integer> getInventoryProperty();
		Property<Timestamp> getIssueTimeProperty();
		Property<Integer> getNumberProperty();
		Property<String> getSeriesProperty();
		Property<Byte> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<Integer> getSourceWarehouseProperty();
		Property<Integer> getTargetWarehouseProperty();
	}
	
	public interface WarehouseTransferDetailProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getWarehouseTransferProperty();
		Property<Integer> getItemProperty();
		Property<Double> getQuantityProperty();
	}
	
	public interface TaskProperties extends AuditProperties{
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
		Property<Integer> getParentProperty();
	}
	
	public interface TaskTagProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTagProperty();
		Property<Integer> getTaskProperty();
	}
	
	public interface TaskCommentProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskProperty();
		Property<String> getCommentProperty();
		Property<Integer> getSourceProperty();
		Property<Integer> getSourceIdProperty();
	}
	
	public interface TaskEventProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskProperty();
		Property<String> getEventProperty();
	}
	
	public interface TaskHolderWorkgroupProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTaskHolderProperty();
		Property<Integer> getWorkgroupProperty();
	}
	
	public interface TaskHolderProperties extends RegistryProperties{
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
	
	public interface ProjectReservationProperties extends AuditProperties{
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
		Property<String> getCreditCardNumberProperty();
		Property<String> getCreditCardExpirationMonthProperty();
		Property<String> getCreditCardExpirationYearProperty();
		Property<String> getCreditCardTypeProperty();
		Property<String> getPenaltyValueProperty();	
		Property<Byte> getTouristTaxFreeProperty();
		Property<Byte> getCheckStatusProperty();
		Property<String> getCancellationUserProperty();
		Property<Timestamp> getCancellationDateProperty();
		Property<String> getTokenProperty();
		Property<Double> getPenaltyAmountProperty();
		Property<Timestamp> getPenaltyDateProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface ProjectCommercialProperties{
		// PROJECT COMMERCIAL
		Property<Integer> getProjectProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTargetProperty();
		Property<Integer> getSellerProperty();
		Property<String> getCommentsProperty();
		Property<Byte> getSourceProperty();
		Property<Byte> getStatusProperty();
		Property<Date> getStatusDateProperty();
		Property<Integer> getProbabilityProperty();
		// PROJECT
		Property<Date> getDateProperty();
		Property<String> getNameProperty();
		
		
	}
	
	public interface CustomerProperties extends RegistryProperties, AuditProperties{
		Property<Integer> getRegistryProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getTariffProperty();
		Property<Byte> getSurchargeProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getEInvoiceProperty();
		Property<Integer> getInvoicingGroupProperty();
		Property<Byte> getProjectGroupedProperty();
		Property<Byte> getDeliveryGroupedProperty();
		Property<Byte> getDeliveryValuatedProperty();
		Property<Integer> getAccountProperty();
	}
	
	public interface SellerProperties extends RegistryProperties{
		Property<Integer> getRegistryProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getCommissionTypeProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
	}
	
	public interface CarrierPackingProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getStatusProperty();
		Property<Timestamp> getIssueDateProperty();
		Property<Integer> getCarrierProperty();
		Property<Timestamp> getDeliveryDateProperty();
		Property<String> getCarrierReferenceProperty();
		Property<String> getNumberPlateProperty();
		Property<String> getDriverNameProperty();
		Property<String> getDriverDocumentProperty();
		
		Property<Double> getGrossWeightProperty();
		Property<Double> getTareProperty();
		Property<Double> getNetProperty();
		Property<Timestamp> getReceptionStartDateProperty();
		Property<Timestamp> getReceptionEndDateProperty();
	}
	
	public interface RegistryProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getDocumentProperty();
		Property<Byte> getDocumentTypeProperty();
		Property<String> getDocumentCountryProperty();
		Property<String> getNameProperty();
		Property<String> getAliasProperty();
		Property<Byte> getTypeProperty();
		Property<String> getNationalityProperty();
		Property<Byte> getSecurityLevelProperty();
	}
	
	public interface CarrierProperties extends RegistryProperties{
		Property<Integer> getScopeProperty();
	}
	
	public interface SupplierProperties extends RegistryProperties, AuditProperties{
		Property<Integer> getTariffProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getWithholdingFarmerProperty();
		Property<Byte> getVatAccrualPaymentProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getPurchaseValuatedProperty();
		Property<Integer> getAccountProperty();
	}

	public interface TargetProperties extends RegistryProperties, AuditProperties{
		Property<Integer> getTariffProperty();
		Property<Byte> getAdvertisingProperty();
		Property<Byte> getSurchargeProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
	}

	public interface PurchaseProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProjectProperty();
		Property<Integer> getSupplierProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();
		Property<String> getPurchaseReferenceProperty();
		Property<Integer> getAddressProperty();
		Property<String> getDiscountExprProperty();
		Property<Date> getIssueDateProperty();
		Property<Integer> getPayMethodProperty();
		Property<Byte> getDocumentTypeProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Byte> getStatusProperty();
		Property<String> getCommentsProperty();
		Property<String> getRemarksProperty();
		Property<Integer> getWorkplaceProperty();
		Property<Integer> getWarehouseProperty();
		Property<Integer> getScopeProperty();
		Property<Short> getNumberOfPymntsProperty();
		Property<Short> getDaysToFirstPymntProperty();
		Property<Short> getDaysBetweenPymntsProperty();
		Property<String> getPymntDaysProperty();
		Property<String> getBankAccountProperty();
		Property<String> getBankAliasProperty();
		Property<String> getBicProperty();
		Property<Byte> getEmailCommunicationProperty();
		Property<Integer> getCarrierProperty();
		Property<String> getShippingAlternativeAddressProperty();
		Property<String> getShippingAlternativeAddress2Property();
		Property<String> getShippingAlternativeZipProperty();
		Property<String> getShippingAlternativeCityProperty();
		Property<String> getShippingAlternativePhoneProperty();
		Property<String> getShippingAlternativeRecipientProperty();
		Property<String> getShippingContactProperty();
		Property<Byte> getShippingPeriodProperty();
		Property<Integer> getCarrierPackingProperty();
		
		// VALUE OF PURCHASE TABLE
		Property<Date> getStartIssueDateProperty();
		Property<Date> getEndIssueDateProperty();
		Property<Byte> getConfidentialProperty();
		
		// REGISTRY
		Property<String> getRegistryNameProperty();
		Property<String> getRegistryDocumentProperty();
		
	}
	
	public interface PurchaseDetailProperties{
		
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getPurchaseProperty();
		Property<Integer> getProjectProperty();
		Property<Integer> getItemProperty();
		Property<Short> getLineProperty();
		Property<String> getDescriptionProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getPriceProperty();
		Property<String> getDiscountExpressionProperty();
		Property<Double> getTaxesProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getProposalDetailProperty();
		Property<Byte> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<Double> getDeliveredProperty();
		Property<Integer> getCarrierPackingProperty();
		
		// VALUE OF PURCHASE TABLE
		Property<Integer> getScopeProperty();
		Property<Byte> getConfidentialProperty();
		Property<Integer> getSupplierProperty();
		
	}
	
	public interface RecordDataProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getRegistryProperty();
		Property<Date> getCreationDateProperty();
		Property<String> getDescriptionProperty();
		Property<String> getNotaryProperty();
		Property<String> getNumberProperty();
		Property<Date> getRecordDateProperty();
		Property<String> getVolumeProperty();
		Property<String> getSectionProperty();
		Property<String> getPageProperty();
		Property<String> getSheetProperty();
		Property<String> getRegistrationProperty();
		Property<Integer> getAttachProperty();
	}
	
	public interface CompanyProperties extends RegistryProperties{
		Property<Integer> getDomainProperty();
		Property<Integer> getDomainParentProperty();
		Property<Byte> getActiveProperty();
		Property<Byte> getSurchargeProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getVatAccrualPaymentProperty();
		Property<Byte> getEInvoiceProperty();
		Property<Integer> getScopeIdProperty();
		Property<String> getScopeDescriptionProperty();	
	}
	
	public interface PersonProperties extends RegistryProperties{
		Property<Integer> getDomainProperty();
		Property<Date> getBirthDateProperty();
		Property<Byte> getGenderProperty();
		Property<Byte> getMaritalStatusProperty();
		Property<String> getSocialSecurityNumProperty();
		Property<String> getFirstNameProperty();
		Property<String> getFirstSurnameProperty();
		Property<String> getSecondSurnameProperty();
	}
	
	public interface ProductTagProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProductProperty();
		Property<Integer> getTagProperty();		
	}
	
	public interface IncomeProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getProjectProperty();
		Property<String> getReferenceCodeProperty();
		Property<Integer> getSupplierProperty();
		Property<Integer> getAddressProperty();
		Property<Date> getIssueTimeProperty();
		Property<Integer> getPayMethodProperty();
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
		Property<Integer> getCarrierPackingProperty();
		
		Property<Byte> getConfidentialProperty();
	}

	public interface IncomeDetailProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getIncomeProperty();
		Property<Integer> getProjectProperty();
		Property<Short> getLineProperty();
		Property<Integer> getItemProperty();
		Property<String> getDescriptionProperty();
		Property<Integer> getWarehouseProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getPriceProperty();
		Property<String> getDiscountExprProperty();
		Property<Integer> getPurchaseDetailProperty();
	}
	
	public interface DataResponseProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNumberProperty();
		Property<Date> getIssueDateProperty();
		Property<Byte> getSourceProperty();
		Property<Integer> getSourceIdProperty();
		Property<String> getCodeProperty();
		
		// DATA RESPONSE DETAIL
		Property<String> getDetailVariableProperty();
		Property<String> getDetailValueProperty();
		
	}
	
	public interface DataResponseDetailProperties extends AuditProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getDataResponseProperty();
		Property<String> getDataVariableProperty();
		Property<String> getValueProperty();
	}

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
		Property<Byte> getConfidentialProperty();
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
	
	public interface SalesDetailProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getSalesProperty();
		Property<Integer> getItemProperty();
		Property<Short> getLineProperty();
		Property<String> getDescriptionProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getPriceProperty();
		Property<String> getdiscountExpressionProperty();
		Property<Double> getTaxesProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getOfferDetailProperty();
		Property<Double> getDeliveredProperty();
	}
	
	public interface CategoryProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Byte> getTypeProperty();
		Property<Integer> getScopeProperty();
		Property<String> getDescriptionProperty();
		Property<String> getUrlProperty();
		Property<Integer> getRattachProperty();
	}
	
	public interface ContractProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getPersonProperty();
		Property<Integer> getWorkplaceProperty();
		Property<Integer> getEnterpriseCCCProperty();
		Property<Date> getStartDateProperty();
		Property<Date> getEndDateProperty();
		Property<Integer> getCalendarProperty();
		Property<String> getDescriptionProperty();
		Property<Byte> getSepeStatusProperty();
		Property<Integer> getRegistrationProperty();
		Property<Date> getSeniorityDateProperty();
		Property<Integer> getEnterpriseActivityProperty();
		Property<Byte> getSSRegimeProperty();
		Property<Byte> getModelProperty();
		Property<Integer> getAgreementLevelProperty();
		Property<String> getCategoryDescriptionProperty();
		Property<Byte> getSSStatusProperty();
	}
	
	public interface ContractDataProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Integer> getContractProperty();
		Property<String> getExpressionProperty();
		Property<Date> getStartDateProperty();
		Property<Date> getEndDateProperty();
	}
	
	public interface IrpfDataProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getContractProperty();
		Property<Byte> getFamilySituationProperty();
		Property<String> getSpouseDocumentProperty();
		Property<Byte> getDisabiltyLevelProperty();
		Property<Byte> getDependenceProperty();
		Property<Date> getMovingDateProperty();
		Property<Byte> getLabourProlongationProperty();
		Property<Byte> getDescendientCountProperty();
		Property<Date> getStartDateProperty();
		Property<Date> getEndDateProperty();
		Property<Byte> getFiscalExclusionProperty();
		Property<Date> getIssueDateProperty();
		Property<Double> getAnnualRemunerationProperty();
		Property<Double> getIrregular182ReductionProperty();
		Property<Double> getIrregular183ReductionProperty();
		Property<Double> getDeducciblesExpensesProperty();
		Property<Double> getSpousalSupportProperty();
		Property<Double> getFoodAnnuityProperty();
		Property<Byte> getDeductHomeLoanProperty();
		Property<Double> getRequestIrpfProperty();
		Property<Byte> getContractTypeProperty();
		Property<Byte> getCeutaMelillaProperty();
	}
	
	public interface AgreementLevelCategoryProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getAgreementLevelProperty();
		Property<String> getDescriptionProperty();
	}
	
	public interface InventoryProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Date> getInventoryDateProperty();
		Property<Integer> getWarehouseProperty();
		Property<String> getDescriptionProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface InventoryDetailProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getInventoryProperty();
		Property<Integer> getItemProperty();
		Property<Double> getActualQuantityProperty();
		Property<Double> getRealQuantityProperty();
		Property<Double> getCostProperty();
	}
	
	public interface CommissionProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Date> getStartDateProperty();
		Property<Date> getEndDateProperty();
	}
	
	public interface CommissionTypeProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<Double> getRateProperty();
	}
	
	public interface CommissionTypeCommissionProperties extends CommissionProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getCommissionTypeProperty();
		Property<Integer> getCommissionProperty();
	}
	
	public interface CommissionItemProperties extends CommissionProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getCommissionProperty();
		Property<Integer> getItemProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getAmountProperty();
		Property<Double> getRateProperty();
	}
	
	public interface CommissionCategoryProperties extends CommissionProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getCommissionProperty();
		Property<Integer> getCategoryProperty();
		Property<Double> getQuantityProperty();
		Property<Double> getRateProperty();
	}
	
	public interface OfferDetailCommissionProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Double> getCommissionProperty();
		Property<Integer> getOfferDetailProperty();
		Property<Double> getAmountProperty();
		Property<Date> getPayDateProperty();
		
		Property<Date> getDateProperty();
		Property<String> getSerieProperty();
		Property<Integer> getNumberProperty();	
		Property<Integer> getSupplierProperty();
		Property<Integer> getTargetProperty();
		Property<Integer> getSellerProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getStatusProperty();	
	}
	
	public interface InvoiceDetailCommissionProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Double> getCommissionProperty();
		Property<Integer> getInvoiceDetailProperty();
		Property<Double> getAmountProperty();
		Property<Date> getPayDateProperty();
		
		Property<Date> getDateProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();	
		Property<Integer> getRegistryProperty();
		Property<Integer> getSellerProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getStatusProperty();	
	}
	
	public interface AccountEntryProperties {
		
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getAccountPeriodProperty();
		Property<java.util.Date> getEntryDateProperty();
		Property<Byte> getEntryTypeProperty();
		Property<Integer> getJournalProperty();
		Property<Integer> getActivityProperty();
		Property<Byte> getConfidentialProperty();
		Property<String> getCommentsProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}

	public interface AccountEntryDetailProperties extends  AccountEntryProperties {
		
		Property<Integer> getAccountProperty();
		Property<String> getAccountCodeProperty();
		Property<String> getAccountDescriptionProperty();
		Property<String> getConceptProperty();
		Property<Double> getDebitProperty();
		Property<Double> getCreditProperty();
		Property<String> getDocumentNumber();
		Property<Integer> getBalancingAccountProperty();
		Property<String> getBalancingAccountCodeProperty();
		Property<String> getBalancingAccountDescriptionProperty();

	}
	
	public interface AccountingRegistryProperties {
		
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getAccountCodeProperty();
		Property<String> getAccountDescriptionProperty();
		Property<String> getNameProperty();
		Property<Byte> getDocumentTypeProperty();
		Property<String> getDocumentCountryProperty();
		Property<String> getDocumentProperty();
		Property<String> getAliasProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Byte> getStatusProperty();
		
	}
	
	public interface MailTemplateProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getScopeProperty();
		Property<String> getNameProperty();
		Property<Byte> getActiveProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getSubjectProperty();
		Property<String> getWidthProperty();
		Property<String> getTitleColorProperty();
		Property<String> getBackgroundColorProperty();
		Property<Integer> getHeaderTemplateProperty();
		Property<Integer> getFooterTemplateProperty();
	}
	
	public interface UserProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getNameProperty();
		Property<String> getLoginProperty();
		Property<Byte> getActiveProperty();
		Property<Integer> getEnterpriseProperty();
		Property<Integer> getRegistryProperty();
		Property<byte[]> getAuthProperty();
		Property<Byte> getSharedProperty();
	}
	
	public interface RawdocProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Byte> getNatureProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getStatusProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
}
