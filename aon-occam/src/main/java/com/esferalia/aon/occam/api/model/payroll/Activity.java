package com.esferalia.aon.occam.api.model.payroll;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;

public class Activity extends EnterpriseActivity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer domain;
	private Integer enterprise;
	private Date startDate;
	private Date endDate;
	private List<EnterpriseCCC> cccs;

	public Activity() {
		super();
		this.cccs = new ArrayList<>();
	}

	public Integer getDomain() {
		return domain;
	}

	public Activity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Integer getEnterprise() {
		return enterprise;
	}

	public Activity setEnterprise(Integer enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Activity setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Activity setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public List<EnterpriseCCC> getCccs() {
		return cccs;
	}
	
	public List<EnterpriseCCC> getActiveCCCs() {
		return cccs.isEmpty() ? cccs : cccs.stream().filter(ccc -> !ccc.isDeleted()).collect(Collectors.toList());
	}

	public Activity setCccs(List<EnterpriseCCC> cccs) {
		this.cccs = cccs;
		return this;
	}
	
	
}
