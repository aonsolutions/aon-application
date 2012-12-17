package com.code.aon.common.domain;

import java.util.Collection;

public interface IDomainSwitcher {

	Integer getDomainId();
	void setDomainId(Integer domainId);
	void addDomainChangeListener( IDomainChangeListener listener);
	void fireBeforeDomainChanged(Integer oldDomain, Integer newDomain);
	void fireAfterDomainChanged(Integer oldDomain, Integer newDomain);
	Integer getParentDomainId();
	boolean isDomainManagementAvailable();
	boolean isDisableDomainManagement();
	boolean isEnableHeredity();
	Collection<Integer> getDomainFilter();
	
	

}
