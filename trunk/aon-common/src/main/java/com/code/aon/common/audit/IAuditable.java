package com.code.aon.common.audit;

import java.util.Date;

public interface IAuditable {
	
	String AUTH_PRINCIPAL_PROVIDER = "com.code.aon.AuthPrincipalProvider";

	String getCreationUser();
	void setCreationUser(String creationUser);
	
	Date getCreationDate();
	void setCreationDate(Date creationDate);
	
	String getModificationUser();
	void setModificationUser(String modificationUser);
	
	Date getModificationDate();
	void setModificationDate(Date modificationDate);

}
