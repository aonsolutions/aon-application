package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.occam.api.model.payroll.Activity;

@SuppressWarnings("serial")
public class ComunicaEnterpriseSettings implements Serializable {

	private Integer enterpriseId;
	private WorkplaceComunica workplaceComunica;
	private List<Activity> activities;
	private AgreementComunica agreementComunica;
	
	public ComunicaEnterpriseSettings() {
		super();
	}
	
	public ComunicaEnterpriseSettings(Integer enterpriseId, WorkplaceComunica workplaceComunica, List<Activity> activities, AgreementComunica agreementComunica) {
		super();
		this.enterpriseId = enterpriseId;
		this.workplaceComunica = workplaceComunica;
		this.activities = activities;
		this.agreementComunica = agreementComunica;
	}

	public Integer getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public WorkplaceComunica getWorkplaceComunica() {
		return workplaceComunica;
	}

	public void setWorkplaceComunica(WorkplaceComunica workplaceComunica) {
		this.workplaceComunica = workplaceComunica;
	}

	public List<Activity> getActivities() {
		return activities;
	}

	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	public AgreementComunica getAgreementComunica() {
		return agreementComunica;
	}

	public void setAgreementComunica(AgreementComunica agreementComunica) {
		this.agreementComunica = agreementComunica;
	}

}
