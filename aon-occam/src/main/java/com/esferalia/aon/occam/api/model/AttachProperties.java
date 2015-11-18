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
	Property<Integer> getCategoryProperty();
	Property<Timestamp> getCreationDateProperty();
	Property<String> getCreationUserProperty();
	Property<byte[]> getDataProperty();
	Property<String> getDparentIdProperty();
	Property<String> getDriveIdProperty();
	Property<Byte> getMimeTypeProperty();
	Property<Timestamp> getModificationDateProperty();
	Property<String> getModificationUserProperty();
	Property<Integer> getRegistryProperty();
	Property<Integer> getScopeProperty();
	Property<Byte> getSecurityLevelProperty();	
}
