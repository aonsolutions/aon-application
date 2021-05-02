package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class AgreementComunica implements Serializable {
	
	private Map<Integer, AgreementComunicaInfo> agreements;
	private Map<Integer, AgreementComunicaInfo> deleteAgreements;
	
	public AgreementComunica() {
		super();
		this.agreements = new HashMap<Integer, AgreementComunicaInfo>();
		this.deleteAgreements = new HashMap<Integer, AgreementComunicaInfo>();
	}

	public Map<Integer, AgreementComunicaInfo> getAgreements() {
		return agreements;
	}

	public void setAgreements(Map<Integer, AgreementComunicaInfo> agreements) {
		this.agreements = agreements;
	}
	
	public Map<Integer, AgreementComunicaInfo> getDeletedAgreements() {
		return deleteAgreements;
	}

	public void setDeletedAgreements(Map<Integer, AgreementComunicaInfo> deleteAgreements) {
		this.deleteAgreements = deleteAgreements;
	}

	public void insertAgreement(Integer agreementId, String description, String ssNumber) {
		this.agreements.put(agreementId, new AgreementComunicaInfo(agreementId, description, ssNumber));
	}

	public void deleteAgreement(Integer agreementId) {
		if(agreementId > 0) {
			AgreementComunicaInfo agreementComunicaInfo = this.agreements.get(agreementId);
			this.deleteAgreements.put(agreementId, agreementComunicaInfo);
		}
		this.agreements.remove(agreementId);
	}
	
}
