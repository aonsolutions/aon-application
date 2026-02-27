package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public interface HasAudit extends Serializable {

	String getCreationUser();
	Date getCreationDate();
	String getModificationUser();
	Date getModificationDate();

}
