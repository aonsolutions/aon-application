package com.code.aon.common.domain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ThreadDomainProvider implements IDomainProvider {

	private long threadId; 
	private Integer currentDomain;
	private Integer userDomain;
	private boolean parentDomain;
	private boolean domainManagementAvailable;
	private List<Integer> filter = new LinkedList<Integer>(); 
	
	public ThreadDomainProvider(long threadId ,Integer currentDomain,Integer userDomain,boolean parentDomain,boolean domainManagementAvailable) {
		this.threadId = threadId; 
		this.currentDomain = currentDomain;
		this.userDomain = userDomain;
		this.parentDomain = parentDomain;
		this.domainManagementAvailable = domainManagementAvailable;
		filter.add(this.currentDomain);
	}

	@Override
	public Integer getCurrentDomain() {
		return this.currentDomain;
	}
	
	@Override
	public Integer getUserDomain() {
		return this.userDomain;
	}

	@Override
	public boolean accept() {
		return (threadId == Thread.currentThread().getId());
	}

	@Override
	public boolean isParentDomain() {
		return this.parentDomain;
	}

	@Override
	public boolean isDomainManagementAvailable() {
		return this.domainManagementAvailable;
	}

	@Override
	public Collection<Integer> getDomainFilter() {
		return filter;
	}
}
