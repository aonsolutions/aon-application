package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnterpriseContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Workplace> workplaces;
	private ActivitiesCCC activitiesCCC;
	private List<Agreement> agreements;
	private Map<String, String> payMethods;
	private Map<Integer, String> scopes;
	
	public EnterpriseContext() {
		super();
		this.workplaces = Collections.emptyList();
		this.activitiesCCC = new ActivitiesCCC();
		this.agreements = Collections.emptyList();
		this.payMethods = Collections.emptyMap();
	}

	public List<Workplace> getWorkplaces() {
		return workplaces;
	}

	public EnterpriseContext setWorkplaces(List<Workplace> workplaces) {
		this.workplaces = workplaces;
		return this;
	}

	public ActivitiesCCC getActivitiesCCC() {
		return activitiesCCC;
	}

	public EnterpriseContext setActivitiesCCC(ActivitiesCCC activitiesCCC) {
		this.activitiesCCC = activitiesCCC;
		return this;
	}

	public List<Agreement> getAgreements() {
		return agreements;
	}

	public EnterpriseContext setAgreements(List<Agreement> agreements) {
		this.agreements = agreements;
		return this;
	}

	public Map<String, String> getPayMethods() {
		return payMethods;
	}

	public EnterpriseContext setPayMethods(Map<String, String> payMethods) {
		this.payMethods = payMethods;
		return this;
	}
	
	public Map<Integer, String> getScopes() {
		return scopes;
	}

	public EnterpriseContext setScopes(Map<Integer, String> scopes) {
		this.scopes = scopes;
		return this;
	}
	
}
