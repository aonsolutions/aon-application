package com.code.aon.common.domain;

public class UniqueDomainProvider implements IDomainProvider {

	public UniqueDomainProvider() {
		System.out.println( " ¡¡ ThreadLocalDomainProvider INITIALIZED !!" );
	}

	@Override
	public Integer getCurrentDomain() {
		return 1;
	}

	@Override
	public boolean accept() {
		return true;
	}

}
