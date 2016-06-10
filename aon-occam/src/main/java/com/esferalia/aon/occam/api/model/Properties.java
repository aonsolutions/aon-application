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
}
