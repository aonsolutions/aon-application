package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.watson.util.AonUtils;

public class AgreementKey {
	
	private Integer id;
	private Integer domain;
	private Integer regime;
	
	
	
	public AgreementKey(Integer id, Integer domain, Integer regime) {
		this.id = id;
		this.domain = domain;
		this.regime = regime;
	}

	public Integer getId() {
		return id;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public Integer getRegime() {
		return regime;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof AgreementKey 
			&& AonUtils.equals(regime, ((AgreementKey)obj).regime) 
			&& AonUtils.equals(domain, ((AgreementKey)obj).domain) 
			&& AonUtils.equals(id,((AgreementKey)obj).id)
			;
	}
	
	@Override
	public int hashCode() {
		return AonUtils.hashCode(regime) + AonUtils.hashCode(domain) + AonUtils.hashCode(id);
	}
}
