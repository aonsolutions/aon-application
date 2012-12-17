package com.code.aon.common.domain;

import java.util.Collection;


public interface IDomainProvider {

	public boolean accept();
	public Integer getCurrentDomain();
	public Integer getUserDomain();
	public Integer getParentDomain();
	public boolean isDomainManagementAvailable();
	public boolean isEnableHeredity();
	public Collection<Integer> getDomainFilter();
	
}