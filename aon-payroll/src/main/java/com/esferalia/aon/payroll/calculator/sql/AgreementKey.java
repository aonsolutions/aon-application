package com.esferalia.aon.payroll.calculator.sql;

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
	

}
