package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class PayrollWorkplace implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer workplace;
	private Integer agreement;
	private Integer enterpriseActivity;
	private Integer calendar;
	
	public Integer getId() {
		return id;
	}
	
	public PayrollWorkplace setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public PayrollWorkplace setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getWorkplace() {
		return workplace;
	}
	
	public PayrollWorkplace setWorkplace(Integer workplace) {
		this.workplace = workplace;
		return this;
	}
	
	public Integer getAgreement() {
		return agreement;
	}
	
	public PayrollWorkplace setAgreement(Integer agreement) {
		this.agreement = agreement;
		return this;
	}
	
	public Integer getCalendar() {
		return calendar;
	}
	
	public PayrollWorkplace setCalendar(Integer calendar) {
		this.calendar = calendar;
		return this;
	}
	
	public Integer getEnterpriseActivity() {
		return enterpriseActivity;
	}
	
	public PayrollWorkplace setEnterpriseActivity(Integer enterpriseActivity) {
		this.enterpriseActivity = enterpriseActivity;
		return this;
	}
}
