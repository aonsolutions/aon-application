package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class ContractInfo implements Serializable{
	
	//Contract Table
	private Integer contractId;
	private Integer workplaceId;
	private String workplaceName;
	private String workplaceZIP;
	private String workplaceFullAddress;
	private Integer cccId;
	private String completeCCC;
	private Date startDate;
	private Date endDate;
	private Date seniorityDate;
	private Integer activityId;
	private String enterpriseCIF;
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
	
	// Comunic@ Fields
	private String colectiveAgreement;
	
	private Integer md_ctzId;
	private String md_ctz;
	
	private Integer partialityCoefId;
	private Double partialityCoef;
	
	private Integer salariesCount;
	private ArrayList<ContractSalaryInfo> contractSalariesInfo;
	
	public ContractInfo() {
		super();
		this.contractId = null;
		this.workplaceId = null;
		this.workplaceName = null;
		this.workplaceFullAddress = null;
		this.workplaceZIP = null;
		this.cccId = null;
		this.completeCCC = null;
		this.startDate = null;
		this.endDate = null;
		this.seniorityDate = null;
		this.activityId = null;
		this.enterpriseCIF = null;
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
		
		this.colectiveAgreement = null;
		this.md_ctz = null;
		this.partialityCoef = null;
		
		this.salariesCount = null;
		this.contractSalariesInfo = new ArrayList<ContractSalaryInfo>();
	}
	
	// ------------- GETTERS / SETTERS -------------

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getEnterpriseCIF() {
		return enterpriseCIF;
	}

	public void setEnterpriseCIF(String enterpriseCIF) {
		this.enterpriseCIF = enterpriseCIF;
	}

	public Integer getCccId() {
		return cccId;
	}

	public void setCccId(Integer cccId) {
		this.cccId = cccId;
	}

	public String getCompleteCCC() {
		return completeCCC;
	}

	public void setCompleteCCC(String completeCCC) {
		this.completeCCC = completeCCC;
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
	
	public String getWorkplaceName() {
		return workplaceName;
	}

	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}

	public String getWorkplaceZIP() {
		return workplaceZIP;
	}

	public void setWorkplaceZIP(String workplaceZIP) {
		this.workplaceZIP = workplaceZIP;
	}

	public String getWorkplaceFullAddress() {
		return workplaceFullAddress;
	}

	public void setWorkplaceFullAddress(String workplaceFullAddress) {
		this.workplaceFullAddress = workplaceFullAddress;
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
	
//	public void setContractType(String contractType) {
//		this.contractType = contractType;
//	}

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
	
	public String getAgreementColective() {
		return colectiveAgreement;
	}

	public void setAgreementColective(String colectiveAgreement) {
		this.colectiveAgreement = colectiveAgreement;
	}

	public Integer getMdctzId() {
		return md_ctzId;
	}

	public void setMdctzId(Integer md_ctzId) {
		this.md_ctzId = md_ctzId;
	}
	
	public String getMdctz() {
		return md_ctz;
	}

	public void setMdctz(String md_ctz) {
		this.md_ctz = md_ctz;
	}

	public Double getPartialityCoef() {
		return partialityCoef;
	}

	public void setPartialityCoef(Double coef) {
		this.partialityCoef = coef;
	}

	public Integer getPartialityCoefId() {
		return partialityCoefId;
	}

	public void setPartialityCoefId(Integer partialityCoefId) {
		this.partialityCoefId = partialityCoefId;
	}

	public void setSalariesCount(Integer salariesCount) {
		this.salariesCount = salariesCount;
	}
	
	public Integer getSalariesCount() {
		return this.salariesCount;
	}
	
	public ArrayList<ContractSalaryInfo> getContractSalariesInfo() {
		return contractSalariesInfo;
	}

	public void setContractSalariesInfo(ArrayList<ContractSalaryInfo> contractSalariesInfo) {
		this.contractSalariesInfo = contractSalariesInfo;
	}

	public String toString(){
		String result = "";
		
		result += "---------------- CONTRACT INFO ---------------- \n";
		
		if(ssRegimen == null || ssRegimen == (byte)3)
			result += toStringFreelancerContract();
		else
			result += toStringContract();
		
		result += "\n";
		
		return result;
		
	}

	private String toStringFreelancerContract() {
		String result = "";
		
		result += "SS Regimen : " + ssRegimen + "\n";
		result += "Workplace Id : " + workplaceId + "\n";
		result += "Start Date : " + startDate + "\n";
		result += "End Date : " + endDate + "\n";
		result += "Seniority Date : " + seniorityDate + "\n";
		result += "Agreement Id : " + agreementId + "\n";
		result += "Agreement Colective : " + colectiveAgreement + "\n";
		result += "Agreement Level Id : " + agreementLevelId + "\n";
		result += "Agreement Category : " + agreementCategory + "\n";
		result += "Journey Type : " + journeyType + "\n";
		
		return result;
	}
	
	private String toStringContract() {
		String result = "";
		
		result += "SS Regimen : " + ssRegimen + "\n";
		result += "Activity Id : " + activityId + "\n";
		result += "CCC Id : " + cccId + "\n";
		result += "CCC Type : " + cccType + "\n";
		
		if(cccType == (byte) 7) result += "MdCTZ : " + md_ctz + "\n";
		
		result += "Workplace Id : " + workplaceId + "\n";
		result += "ContractType : " + contractType + "\n";
		result += "Contract Modality : " + contractModel + "\n";
		result += "Start Date : " + startDate + "\n";
		result += "End Date : " + endDate + "\n";
		result += "Seniority Date : " + seniorityDate + "\n";
		result += "Agreement Id : " + agreementId + "\n";
		result += "Agreement Colective : " + colectiveAgreement + "\n";
		result += "Agreement Level Id : " + agreementLevelId + "\n";
		result += "Agreement Category : " + agreementCategory + "\n";
		result += "Quote Group : " + quoteGroup + "\n";
		result += "Ocupation : " + ocupation + "\n";
		result += "Partiality Coef : " + partialityCoef + "\n";
		
		return result;
	}

}