package com.esferalia.aon.occam.api.model;

import java.sql.Date;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface Properties {

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
}
