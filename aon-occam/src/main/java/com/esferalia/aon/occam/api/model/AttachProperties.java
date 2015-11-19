package com.esferalia.aon.occam.api.model;

import java.sql.Date;
import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.Filter.Property;

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
