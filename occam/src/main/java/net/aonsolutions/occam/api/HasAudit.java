package net.aonsolutions.occam.api;

import java.util.Date;
import java.util.Optional;

public interface HasAudit<T> {

	Optional<String> getCreationUser();
	T setCreationUser( String user);
	
	Optional<Date> getCreationDate();
	T setCreationDate( Date creationDate);
	
	Optional<String> getModificationUser();
	T setModificationUser( String user);
	
	Optional<Date> getModificationDate();
	T setModificationDate( Date creationDate);

}
