package com.code.aon.common.domain;

import java.util.Collection;

public interface IDomainSwitcher {

	Integer getDomainId();
	void setDomainId(Integer domainId);
	void addDomainChangeListener( IDomainChangeListener listener);
	void fireBeforeDomainChanged(Integer oldDomain, Integer newDomain);
	void fireAfterDomainChanged(Integer oldDomain, Integer newDomain);
	boolean isParentDomain();
	boolean isDomainManagementAvailable();
	Collection<Integer> getDomainFilter();
	
	

}
