package com.code.aon.common.domain;

import java.util.Collection;


public interface IDomainProvider {

	public boolean accept();
	public Integer getCurrentDomain();
	public Integer getUserDomain();
	public boolean isParentDomain();
	public boolean isDomainManagementAvailable();
	public Collection<Integer> getDomainFilter();
	
}