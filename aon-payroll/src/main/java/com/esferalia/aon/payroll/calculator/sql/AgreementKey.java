package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.watson.util.AonUtils;

public class AgreementKey {
	
	private Integer id;
	private Integer domain;
	
	
	
	public AgreementKey(Integer id, Integer domain) {
		this.id = id;
		this.domain = domain;
	}

	public Integer getId() {
		return id;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AgreementKey 
			&& AonUtils.equals(domain, ((AgreementKey)obj).domain) 
			&& AonUtils.equals(id,((AgreementKey)obj).id)
			;
	}
	
	@Override
	public int hashCode() {
		return AonUtils.hashCode(domain) + AonUtils.hashCode(id);
	}
}
