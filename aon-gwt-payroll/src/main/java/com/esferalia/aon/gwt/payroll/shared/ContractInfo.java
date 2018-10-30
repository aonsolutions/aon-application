package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ContractInfo implements Serializable{
	
	//Contract Table
	private Integer contractId;
	private Integer workplaceId;
	private Integer cccId;
	private Date startDate;
	private Date endDate;
	private Date seniorityDate;
	private Integer activityId;
	private Byte ssRegimen;
	private String agreementCategory;
	private Integer agreementLevelId;
	private Integer agreementId; //¿Innecesario? Posiblemente por que tenemos el nivel
	
	//Enterprise CCC Table
	private Byte cccType;
	
	//Contract Data Table
	private Integer contracttypeId;
	private String contractType;
	private Integer quotegroupId;
	private String quoteGroup;
	private Integer ocupationId;
	private String ocupation;
	private Integer journeytypeId;
	private Byte journeyType;
	
	//Contract Info Table
	private Integer contractmodelId;
	private String contractModel;
	
	public ContractInfo(){
		super();
	}
	
	// ------------- GETTERS / SETTERS -------------

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getCccId() {
		return cccId;
	}

	public void setCccId(Integer cccId) {
		this.cccId = cccId;
	}

	public Byte getCccType() {
		return cccType;
	}

	public void setCccType(Byte cccType) {
		this.cccType = cccType;
	}

	public Integer getWorkplaceId() {
		return workplaceId;
	}

	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getContractModel() {
		return contractModel;
	}

	public void setContractModel(String contractModel) {
		this.contractModel = contractModel;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getSeniorityDate() {
		return seniorityDate;
	}

	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
	}

	public Integer getAgreementId() {
		return agreementId;
	}

	public void setAgreementId(Integer agreementId) {
		this.agreementId = agreementId;
	}

	public Integer getAgreementLevelId() {
		return agreementLevelId;
	}

	public void setAgreementLevelId(Integer agreementLevelId) {
		this.agreementLevelId = agreementLevelId;
	}

	public String getAgreementCategory() {
		return agreementCategory;
	}

	public void setAgreementCategory(String agreementCategory) {
		this.agreementCategory = agreementCategory;
	}

	public String getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public String getOcupation() {
		return ocupation;
	}

	public void setOcupation(String ocupation) {
		this.ocupation = ocupation;
	}

	public Byte getJourneyType() {
		return journeyType;
	}

	public void setJourneyType(Byte journeyType) {
		this.journeyType = journeyType;
	}

	public Byte getSsRegimen() {
		return ssRegimen;
	}

	public void setSsRegimen(Byte ssRegimen) {
		this.ssRegimen = ssRegimen;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public Integer getContracttypeId() {
		return contracttypeId;
	}

	public void setContracttypeId(Integer contracttypeId) {
		this.contracttypeId = contracttypeId;
	}

	public Integer getQuotegroupId() {
		return quotegroupId;
	}

	public void setQuotegroupId(Integer quotegroupId) {
		this.quotegroupId = quotegroupId;
	}

	public Integer getOcupationId() {
		return ocupationId;
	}

	public void setOcupationId(Integer ocupationId) {
		this.ocupationId = ocupationId;
	}

	public Integer getJourneytypeId() {
		return journeytypeId;
	}

	public void setJourneytypeId(Integer journeytypeId) {
		this.journeytypeId = journeytypeId;
	}

	public Integer getContractmodelId() {
		return contractmodelId;
	}

	public void setContractmodelId(Integer contractmodelId) {
		this.contractmodelId = contractmodelId;
	}

}