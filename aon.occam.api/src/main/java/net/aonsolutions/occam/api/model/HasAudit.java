package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;

public interface HasAudit {

	String getCreationUser();
	Timestamp getCreationDate();
	String getModificationUser();
	Timestamp getModificationDate();

}
