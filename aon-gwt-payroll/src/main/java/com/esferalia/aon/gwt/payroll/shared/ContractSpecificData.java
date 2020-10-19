package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

public class ContractSpecificData implements Serializable {
	
	private Integer id;
	private String cno;
	private Date calendarFormativeStartDate;
	private Date calendarFormativeEndDate;
	private String formativeLevel;
	private String academicTitulation;
	private Boolean profesionality; 
	private String signBasicCopy;
	private String basicCopy;
	private String useEnterpriseFree;
	private String agreementHours;
	private String agreementMinutes;
	private Boolean repeatFD;
	private String journeyType;
	private String journeyDurationHours;
	private String journeyDurationMinutes;
	private Boolean teoricFormation;
	private String formationHours;
	private String formationMinutes;
	private String retirementPercent;
	private Boolean workProgramData;
	private String workProgram;
	private Boolean temporalWorkEnterprise; 
	private String nif;
	private String socialReason;
	private Boolean contractTemplate;
	private Boolean foreignEnterprise; 
	private Boolean contractRelief;
	private String reliefEmployee;
	private String retirementName;
	private String retirementSurname;
	private String retirementSurname2;
	private Boolean offerWorkData;
	private String offer;
	private Boolean workshopSchoolB;
	private String workshopSchool;
	private Boolean disabilityB;
	private String disability;
	private Boolean annexedB;
	private Boolean annexed;
	private Boolean annexed2;
	private String sourceYear;
	private Boolean campaigns;
	private String cpCampaign;
	private String codeCampaign;
	private String yearCampaign;
	private Boolean invest;
	private String employer;
	private String employee;
	private Boolean researcher;
	private Boolean interimCauseB;
	private String interimCause;
	private Boolean entrepreneurSupport;
	private String bonusColective;
	private Boolean freelanceEmployeer;
	private Boolean promotionMeasures;
	private Boolean promotionPermanentHiring;
	private Boolean quoteReductions;
	private String reductionColective;
	private Boolean quoteReduction;
	private Boolean quoteReduction2;
	private String journeyPercent;
	
	public ContractSpecificData() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCno() {
		return cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	public Date getCalendarFormativeStartDate() {
		return calendarFormativeStartDate;
	}

	public void setCalendarFormativeStartDate(Date calendarFormativeStartDate) {
		this.calendarFormativeStartDate = calendarFormativeStartDate;
	}

	public Date getCalendarFormativeEndDate() {
		return calendarFormativeEndDate;
	}

	public void setCalendarFormativeEndDate(Date calendarFormativeEndDate) {
		this.calendarFormativeEndDate = calendarFormativeEndDate;
	}

	public String getFormativeLevel() {
		return formativeLevel;
	}

	public void setFormativeLevel(String formativeLevel) {
		this.formativeLevel = formativeLevel;
	}

	public String getAcademicTitulation() {
		return academicTitulation;
	}

	public void setAcademicTitulation(String academicTitulation) {
		this.academicTitulation = academicTitulation;
	}

	public Boolean getProfesionality() {
		return profesionality;
	}

	public void setProfesionality(Boolean profesionality) {
		this.profesionality = profesionality;
	}

	public String getSignBasicCopy() {
		return signBasicCopy;
	}

	public void setSignBasicCopy(String signBasicCopy) {
		this.signBasicCopy = signBasicCopy;
	}

	public String getBasicCopy() {
		return basicCopy;
	}

	public void setBasicCopy(String basicCopy) {
		this.basicCopy = basicCopy;
	}

	public String getUseEnterpriseFree() {
		return useEnterpriseFree;
	}

	public void setUseEnterpriseFree(String useEnterpriseFree) {
		this.useEnterpriseFree = useEnterpriseFree;
	}

	public String getAgreementHours() {
		return agreementHours;
	}

	public void setAgreementHours(String agreementHours) {
		this.agreementHours = agreementHours;
	}

	public String getAgreementMinutes() {
		return agreementMinutes;
	}

	public void setAgreementMinutes(String agreementMinutes) {
		this.agreementMinutes = agreementMinutes;
	}

	public Boolean getRepeatFD() {
		return repeatFD;
	}

	public void setRepeatFD(Boolean repeatFD) {
		this.repeatFD = repeatFD;
	}

	public String getJourneyType() {
		return journeyType;
	}

	public void setJourneyType(String journeyType) {
		this.journeyType = journeyType;
	}

	public String getJourneyDurationHours() {
		return journeyDurationHours;
	}

	public void setJourneyDurationHours(String journeyDurationHours) {
		this.journeyDurationHours = journeyDurationHours;
	}

	public String getJourneyDurationMinutes() {
		return journeyDurationMinutes;
	}

	public void setJourneyDurationMinutes(String journeyDurationMinutes) {
		this.journeyDurationMinutes = journeyDurationMinutes;
	}

	public Boolean getTeoricFormation() {
		return teoricFormation;
	}

	public void setTeoricFormation(Boolean teoricFormation) {
		this.teoricFormation = teoricFormation;
	}

	public String getFormationHours() {
		return formationHours;
	}

	public void setFormationHours(String formationHours) {
		this.formationHours = formationHours;
	}

	public String getFormationMinutes() {
		return formationMinutes;
	}

	public void setFormationMinutes(String formationMinutes) {
		this.formationMinutes = formationMinutes;
	}

	public String getRetirementPercent() {
		return retirementPercent;
	}

	public void setRetirementPercent(String retirementPercent) {
		this.retirementPercent = retirementPercent;
	}

	public Boolean getWorkProgramData() {
		return workProgramData;
	}

	public void setWorkProgramData(Boolean workProgramData) {
		this.workProgramData = workProgramData;
	}

	public String getWorkProgram() {
		return workProgram;
	}

	public void setWorkProgram(String workProgram) {
		this.workProgram = workProgram;
	}

	public Boolean getTemporalWorkEnterprise() {
		return temporalWorkEnterprise;
	}

	public void setTemporalWorkEnterprise(Boolean temporalWorkEnterprise) {
		this.temporalWorkEnterprise = temporalWorkEnterprise;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getSocialReason() {
		return socialReason;
	}

	public void setSocialReason(String socialReason) {
		this.socialReason = socialReason;
	}

	public Boolean getContractTemplate() {
		return contractTemplate;
	}

	public void setContractTemplate(Boolean contractTemplate) {
		this.contractTemplate = contractTemplate;
	}

	public Boolean getForeignEnterprise() {
		return foreignEnterprise;
	}

	public void setForeignEnterprise(Boolean foreignEnterprise) {
		this.foreignEnterprise = foreignEnterprise;
	}

	public Boolean getContractRelief() {
		return contractRelief;
	}

	public void setContractRelief(Boolean contractRelief) {
		this.contractRelief = contractRelief;
	}

	public String getReliefEmployee() {
		return reliefEmployee;
	}

	public void setReliefEmployee(String reliefEmployee) {
		this.reliefEmployee = reliefEmployee;
	}

	public String getRetirementName() {
		return retirementName;
	}

	public void setRetirementName(String retirementName) {
		this.retirementName = retirementName;
	}

	public String getRetirementSurname() {
		return retirementSurname;
	}

	public void setRetirementSurname(String retirementSurname) {
		this.retirementSurname = retirementSurname;
	}

	public String getRetirementSurname2() {
		return retirementSurname2;
	}

	public void setRetirementSurname2(String retirementSurname2) {
		this.retirementSurname2 = retirementSurname2;
	}

	public Boolean getOfferWorkData() {
		return offerWorkData;
	}

	public void setOfferWorkData(Boolean offerWorkData) {
		this.offerWorkData = offerWorkData;
	}

	public String getOffer() {
		return offer;
	}

	public void setOffer(String offer) {
		this.offer = offer;
	}

	public Boolean getWorkshopSchoolB() {
		return workshopSchoolB;
	}

	public void setWorkshopSchoolB(Boolean workshopSchoolB) {
		this.workshopSchoolB = workshopSchoolB;
	}

	public String getWorkshopSchool() {
		return workshopSchool;
	}

	public void setWorkshopSchool(String workshopSchool) {
		this.workshopSchool = workshopSchool;
	}

	public Boolean getDisabilityB() {
		return disabilityB;
	}

	public void setDisabilityB(Boolean disabilityB) {
		this.disabilityB = disabilityB;
	}

	public String getDisability() {
		return disability;
	}

	public void setDisability(String disability) {
		this.disability = disability;
	}

	public Boolean getAnnexedB() {
		return annexedB;
	}

	public void setAnnexedB(Boolean annexedB) {
		this.annexedB = annexedB;
	}

	public Boolean getAnnexed() {
		return annexed;
	}

	public void setAnnexed(Boolean annexed) {
		this.annexed = annexed;
	}

	public Boolean getAnnexed2() {
		return annexed2;
	}

	public void setAnnexed2(Boolean annexed2) {
		this.annexed2 = annexed2;
	}

	public String getSourceYear() {
		return sourceYear;
	}

	public void setSourceYear(String sourceYear) {
		this.sourceYear = sourceYear;
	}

	public Boolean getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Boolean campaigns) {
		this.campaigns = campaigns;
	}

	public String getCpCampaign() {
		return cpCampaign;
	}

	public void setCpCampaign(String cpCampaign) {
		this.cpCampaign = cpCampaign;
	}

	public String getCodeCampaign() {
		return codeCampaign;
	}

	public void setCodeCampaign(String codeCampaign) {
		this.codeCampaign = codeCampaign;
	}

	public String getYearCampaign() {
		return yearCampaign;
	}

	public void setYearCampaign(String yearCampaign) {
		this.yearCampaign = yearCampaign;
	}

	public Boolean getInvest() {
		return invest;
	}

	public void setInvest(Boolean invest) {
		this.invest = invest;
	}

	public String getEmployer() {
		return employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public Boolean getResearcher() {
		return researcher;
	}

	public void setResearcher(Boolean researcher) {
		this.researcher = researcher;
	}

	public Boolean getIsInterimCause() {
		return interimCauseB;
	}

	public void setIsInterimCause(Boolean interimCauseB) {
		this.interimCauseB = interimCauseB;
	}

	public String getInterimCause() {
		return interimCause;
	}

	public void setInterimCause(String interimCause) {
		this.interimCause = interimCause;
	}

	public Boolean getEntrepreneurSupport() {
		return entrepreneurSupport;
	}

	public void setEntrepreneurSupport(Boolean entrepreneurSupport) {
		this.entrepreneurSupport = entrepreneurSupport;
	}

	public String getBonusColective() {
		return bonusColective;
	}

	public void setBonusColective(String bonusColective) {
		this.bonusColective = bonusColective;
	}

	public Boolean getFreelanceEmployeer() {
		return freelanceEmployeer;
	}

	public void setFreelanceEmployeer(Boolean freelanceEmployeer) {
		this.freelanceEmployeer = freelanceEmployeer;
	}

	public Boolean getPromotionMeasures() {
		return promotionMeasures;
	}

	public void setPromotionMeasures(Boolean promotionMeasures) {
		this.promotionMeasures = promotionMeasures;
	}

	public Boolean getPromotionPermanentHiring() {
		return promotionPermanentHiring;
	}

	public void setPromotionPermanentHiring(Boolean promotionPermanentHiring) {
		this.promotionPermanentHiring = promotionPermanentHiring;
	}

	public Boolean getQuoteReductions() {
		return quoteReductions;
	}

	public void setQuoteReductions(Boolean quoteReductions) {
		this.quoteReductions = quoteReductions;
	}

	public String getReductionColective() {
		return reductionColective;
	}

	public void setReductionColective(String reductionColective) {
		this.reductionColective = reductionColective;
	}

	public Boolean getQuoteReduction() {
		return quoteReduction;
	}

	public void setQuoteReduction(Boolean quoteReduction) {
		this.quoteReduction = quoteReduction;
	}

	public Boolean getQuoteReduction2() {
		return quoteReduction2;
	}

	public void setQuoteReduction2(Boolean quoteReduction2) {
		this.quoteReduction2 = quoteReduction2;
	}

	public String getJourneyPercent() {
		return journeyPercent;
	}

	public void setJourneyPercent(String journeyPercentTB) {
		this.journeyPercent = journeyPercentTB;
	}
	
}
