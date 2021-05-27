package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ComunicaEnterpriseSettings implements Serializable {

	private Integer enterpriseId;
	private WorkplaceComunica workplaceComunica;
	private MainCCCInfo mainCCCInfo;
	private AgreementComunica agreementComunica;
	
	public ComunicaEnterpriseSettings() {
		super();
	}
	
	public ComunicaEnterpriseSettings(Integer enterpriseId, WorkplaceComunica workplaceComunica, MainCCCInfo mainCCCInfo, AgreementComunica agreementComunica) {
		super();
		this.enterpriseId = enterpriseId;
		this.workplaceComunica = workplaceComunica;
		this.mainCCCInfo = mainCCCInfo;
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

	public MainCCCInfo getMainCCCInfo() {
		return mainCCCInfo;
	}

	public void setMainCCCInfo(MainCCCInfo mainCCCInfo) {
		this.mainCCCInfo = mainCCCInfo;
	}

	public AgreementComunica getAgreementComunica() {
		return agreementComunica;
	}

	public void setAgreementComunica(AgreementComunica agreementComunica) {
		this.agreementComunica = agreementComunica;
	}

}
