package com.code.aon.common.domain;


public interface IDomainChangeListener {

	public void beforeDomainChanged(DomainEvent event);
	public void afterDomainChanged(DomainEvent event);
	
}