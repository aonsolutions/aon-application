package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
	private Integer contractModel;
	private Integer retaId;
	
	//Contract Journey Duration
	private ContractJourneyDuration contractJourneyDuration;
	
	private Date oldStartDate;
	private Date oldEndDate;
	
	//Has Payroll
	private Boolean hasPayroll;
	private Date payrollDate;
	
	public ContractInfo() {
		super();
		this.contractId = null;
		this.workplaceId = null;
		this.cccId = null;
		this.startDate = null;
		this.endDate = null;
		this.seniorityDate = null;
		this.activityId = null;
		this.ssRegimen = null;
		this.agreementCategory = null;
		this.agreementLevelId = null;
		this.agreementId = null;
		this.cccType = null;
		this.contracttypeId = null;
		this.contractType = null;
		this.quotegroupId = null;
		this.quoteGroup = null;
		this.ocupationId = null;
		this.ocupation = null;
		this.journeytypeId = null;
		this.journeyType = null;
		this.contractmodelId = null;
		this.contractModel = null;
		this.retaId = null;
		this.contractJourneyDuration = new ContractJourneyDuration();
		this.oldStartDate = null;
		this.oldEndDate = null;
		this.hasPayroll = false;
		this.payrollDate = null;
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
		if(null != contractType && contractType.contains("\""))
			this.contractType = contractType.split("\"")[1];
		else
			this.contractType = contractType;
	}

	public Integer getContractModel() {
		return contractModel;
	}

	public void setContractModel(Integer ordinal) {
		this.contractModel = ordinal;
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
		if(null != quoteGroup && quoteGroup.contains("\""))
			this.quoteGroup = quoteGroup.split("\"")[1];
		else
			this.quoteGroup = quoteGroup;
	}

	public String getOcupation() {
		return ocupation;
	}

	public void setOcupation(String ocupation) {
		if(null != ocupation && ocupation.contains("\""))
			this.ocupation = ocupation.split("\"")[1];
		else
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
	
	public Integer getRetaId() {
		return retaId;
	}

	public void setRetaId(Integer retaId) {
		this.retaId = retaId;
	}

	public ContractJourneyDuration getContractJourneyDuration() {
		return contractJourneyDuration;
	}

	public void setContractJourneyDuration(Map<Date, ArrayList<JourneyDuration>> journies) {
		this.contractJourneyDuration.setContractJourneyDuration(journies);
	}
	
	public Date getOldStartDate() {
		return oldStartDate;
	}

	public void setOldStartDate(Date oldStartDate) {
		this.oldStartDate = oldStartDate;
	}

	public Date getOldEndDate() {
		return oldEndDate;
	}

	public void setOldEndDate(Date oldEndDate) {
		this.oldEndDate = oldEndDate;
	}

	public Boolean hasPayroll() {
		return hasPayroll;
	}

	public void setHasPayroll(Boolean hasPayroll) {
		this.hasPayroll = hasPayroll;
	}

	public Date getPayrollDate() {
		return payrollDate;
	}

	public void setPayrollDate(Date payrollDate) {
		this.payrollDate = payrollDate;
	}

	public String toString(){
		String result = "";
		
		result += "---------------- NEW CONTRACT INFO ---------------- \n";
		result += " **** Contract Table **** \n";
		result += "Contract Id : " + contractId + "\n";
		result += "Workplace Id : " + workplaceId + "\n";
		result += "Enterprise CCC Id : " + cccId + "\n";
		result += "Start Date : " + startDate + "\n";
		result += "End Date : " + endDate + "\n";
		result += "Seniority Date : " + seniorityDate + "\n";
		result += "Enterprise Activity Id : " + activityId + "\n";
		result += "SS Regimen : " + ssRegimen + "\n";
		result += "Agreement Category : " + agreementCategory + "\n";
		result += "Agreement Level Id : " + agreementLevelId + "\n";
		result += "Agreement Id : " + agreementId + "\n";
		result += " **** Enterprise CCC Table **** \n";
		result += "Enterprise CCC Type : " + cccType + "\n";
		result += " **** Contract Data Table **** \n";
		result += "Contract Type Id : " + contracttypeId + "\n";
		result += "Contract Type : " + contractType + "\n";
		result += "Quote Group Id : " + quotegroupId + "\n";
		result += "Quote Group : " + quoteGroup + "\n";
		result += "Ocupation Id : " + ocupationId + "\n";
		result += "Ocupation : "+ ocupation + "\n";
		result += "Journey Type Id : " + journeytypeId + "\n";
		result += "Journey Type : " + journeyType + "\n";
		result += " **** Contract Info Table **** \n";
		result += "Contract Model Id : " + contractmodelId + "\n";
		result += "Contract Model : " + contractModel + "\n";
		result += "Reta Id : " + retaId + "\n";
		result += "Old Start Date : " + oldStartDate + "\n";
		result += "Old End Date : " + oldEndDate + "\n";
		
		return result;
		
	}

}