package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.view.client.ProvidesKey;

public class AgreementsClean implements Serializable {

	private Integer agreementId;
	private String description;
	private String ssNumber;
	private List<AgreementCleanContract> contracts;
	
	//The key provider that provides the unique ID of a AgreementsClean.
    public static final ProvidesKey<AgreementsClean> KEY_PROVIDER = item -> item == null ? null : item.getAgreementId();
	
    public AgreementsClean() {
    	super();
    }
    
	public AgreementsClean(Integer agreementId, String description, String ssNumber) {
		super();
		this.agreementId = agreementId;
		this.description = description;
		this.ssNumber = ssNumber;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSsNumber() {
		return ssNumber;
	}

	public void setSsNumber(String ssNumber) {
		this.ssNumber = ssNumber;
	}

	public List<AgreementCleanContract> getContracts() {
		return contracts;
	}

	public void setContracts(List<AgreementCleanContract> contracts) {
		this.contracts = contracts;
	}
	
	public String getContractsInfo() {

		String message = "";
		
		message = "El convenio <b>" + getDescription() + "</b> contiene contratos asociados.";
		String enterprise = getContracts().get(0).getEnterprise();
		message += "<br><br>";
		message += "<b>" + enterprise + "</b><br><br>";
		for(AgreementCleanContract contract : getContracts()) {
			if(AonStringUtils.equalsIgnoreCase(enterprise, contract.getEnterprise()))
				message += "&emsp;" + contract.getEmployee() + "<br>";
			else {
				message += "<br><b>" + contract.getEnterprise() + "</b><br><br>";
				message += "&emsp;" + contract.getEmployee() + "<br>";
				enterprise = contract.getEnterprise();
			}
		}
		
		return message;
	}
	
}
