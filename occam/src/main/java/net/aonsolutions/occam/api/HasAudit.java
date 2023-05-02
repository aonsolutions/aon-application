package net.aonsolutions.occam.api;

import java.util.Date;

public interface HasAudit<T> {

	String getCreationUser();
	T setCreationUser( String user);
	
	Date getCreationDate();
	T setCreationDate( Date creationDate);
	
	String getModificationUser();
	T setModificationUser( String user);
	
	Date getModificationDate();
	T setModificationDate( Date creationDate);

}
