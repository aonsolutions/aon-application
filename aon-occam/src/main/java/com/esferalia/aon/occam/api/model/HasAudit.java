package com.esferalia.aon.occam.api.model;

import java.util.Date;

public interface HasAudit {

	String getCreationUser();
	Date getCreationDate();
	String getModificationUser();
	Date getModificationDate();

}
