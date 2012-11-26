package com.code.aon.common.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class UniqueDomainProvider implements IDomainProvider {

	private List<Integer> filter = new LinkedList<Integer>(); 
	
	public UniqueDomainProvider() {
		System.out.println( " ¡¡ ThreadLocalDomainProvider INITIALIZED !!" );
		filter.add(1);
	}

	@Override
	public Integer getCurrentDomain() {
		return 1;
	}
	
	@Override
	public Integer getUserDomain() {
		return 1;
	}

	@Override
	public boolean accept() {
		return true;
	}

	@Override
	public boolean isParentDomain() {
		return true;
	}

	@Override
	public boolean isDomainManagementAvailable() {
		return false;
	}

	@Override
	public Collection<Integer> getDomainFilter() {
		return null;
	}
}
