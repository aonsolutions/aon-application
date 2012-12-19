package com.code.aon.common.domain;


public class UniqueDomainProvider implements IDomainProvider {

	@Override
	public Integer getCurrentDomain() {
		return 1;
	}
	
	@Override
	public Integer getUserDomain() {
		return 1;
	}

	@Override
	public boolean isDomainManagementAvailable() {
		return false;
	}

	@Override
	public Integer getParentDomain() {
		return null;
	}

	@Override
	public boolean isEnableHeredity() {
		return false;
	}
	
}
