package com.code.aon.common.domain;



public interface IDomainProvider {

	public Integer getCurrentDomain();
	public Integer getUserDomain();
	public Integer getParentDomain();
	public boolean isDomainManagementAvailable();
	public boolean isEnableHeredity();
	
}