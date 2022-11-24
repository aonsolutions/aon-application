package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ContractSpecificData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String cno;
	private String ide;
	private Date comunicationDate;
	private String transformIde;
	private Date comunicationTransformDate;
	private Map<String, Date> extensions;
	private String calendarFormativeStartDate;
	private String calendarFormativeEndDate;
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
	private String journeyPercent;
	private Boolean bonus;
	private String bonusType;
	
	private Boolean olderThan52;
	private String otherLegislations;
	
	private Boolean disc;
	private String discReason;
	
	private Boolean trueDate;
	private Boolean planRecovery;
	
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

	public String getIde() {
		return ide;
	}

	public void setIde(String ide) {
		this.ide = ide;
	}

	public Date getComunicationDate() {
		return comunicationDate;
	}

	public void setComunicationDate(Date comunicationDate) {
		this.comunicationDate = comunicationDate;
	}
	
	public String getTransformIde() {
		return transformIde;
	}

	public void setTransformIde(String transformIde) {
		this.transformIde = transformIde;
	}

	public Date getComunicationTransformDate() {
		return comunicationTransformDate;
	}

	public void setComunicationTransformDate(Date comunicationTransformDate) {
		this.comunicationTransformDate = comunicationTransformDate;
	}

	public  Map<String, Date> getExtensions() {
		return null == extensions ? new HashMap<String, Date>() : extensions;
	}

	public void setExtensions(Map<String, Date> extensions) {
		this.extensions = extensions;
	}

	public void addExtension(String extensionIde, Date extensionDate) {
		if(null == this.extensions) this.extensions = new HashMap<String, Date>();
		this.extensions.put(extensionIde, extensionDate);
	}
	
	public Date getCalendarFormativeStartDate() {
		return parse(calendarFormativeStartDate);
	}

	public void setCalendarFormativeStartDate(Date calendarFormativeStartDate) {
		this.calendarFormativeStartDate = Shared.format(calendarFormativeStartDate);
	}

	public Date getCalendarFormativeEndDate() {
		return parse(calendarFormativeEndDate);
	}

	public void setCalendarFormativeEndDate(Date calendarFormativeEndDate) {
		this.calendarFormativeEndDate = Shared.format(calendarFormativeEndDate);
	}

	public String getFormativeLevel() {
		return null == formativeLevel ? "" : formativeLevel;
	}

	public void setFormativeLevel(String formativeLevel) {
		this.formativeLevel = formativeLevel;
	}

	public String getAcademicTitulation() {
		return null == academicTitulation ? "" : academicTitulation;
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
		return null == signBasicCopy ? "" : signBasicCopy;
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
		return null == journeyType ? "" : journeyType;
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
		return null == workProgramData ? Boolean.FALSE : workProgramData;
	}

	public void setWorkProgramData(Boolean workProgramData) {
		this.workProgramData = workProgramData;
	}

	public String getWorkProgram() {
		return null == workProgram ? "" : workProgram;
	}

	public void setWorkProgram(String workProgram) {
		this.workProgram = workProgram;
	}

	public Boolean getTemporalWorkEnterprise() {
		return null == temporalWorkEnterprise ? Boolean.FALSE : temporalWorkEnterprise;
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
		return null == contractRelief ? Boolean.FALSE : contractRelief;
	}

	public void setContractRelief(Boolean contractRelief) {
		this.contractRelief = contractRelief;
	}

	public String getReliefEmployee() {
		return null == reliefEmployee ? "" : reliefEmployee;
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
		return null == offerWorkData ? Boolean.FALSE : offerWorkData;
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
		return null == workshopSchoolB ? Boolean.FALSE : workshopSchoolB;
	}

	public void setWorkshopSchoolB(Boolean workshopSchoolB) {
		this.workshopSchoolB = workshopSchoolB;
	}

	public String getWorkshopSchool() {
		return null == workshopSchool ? "" : workshopSchool;
	}

	public void setWorkshopSchool(String workshopSchool) {
		this.workshopSchool = workshopSchool;
	}

	public Boolean getDisabilityB() {
		return null == disabilityB ? Boolean.FALSE : disabilityB;
	}

	public void setDisabilityB(Boolean disabilityB) {
		this.disabilityB = disabilityB;
	}

	public String getDisability() {
		return null == disability ? "" : disability;
	}

	public void setDisability(String disability) {
		this.disability = disability;
	}

	public Boolean getAnnexedB() {
		return null == annexedB ? Boolean.FALSE : annexedB;
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

	public String getSourceYear() {
		return sourceYear;
	}

	public void setSourceYear(String sourceYear) {
		this.sourceYear = sourceYear;
	}

	public Boolean getCampaigns() {
		return null == campaigns ? Boolean.FALSE : campaigns;
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
		return null == invest ? Boolean.FALSE : invest;
	}

	public void setInvest(Boolean invest) {
		this.invest = invest;
	}

	public String getEmployer() {
		return null == employer ? "" : employer;
	}

	public void setEmployer(String employer) {
		this.employer = employer;
	}

	public String getEmployee() {
		return null == employee ? "" : employee;
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
		return null == interimCauseB ? Boolean.FALSE : interimCauseB;
	}

	public void setIsInterimCause(Boolean interimCauseB) {
		this.interimCauseB = interimCauseB;
	}

	public String getInterimCause() {
		return null == interimCause ? "" : interimCause;
	}

	public void setInterimCause(String interimCause) {
		this.interimCause = interimCause;
	}

	public Boolean getEntrepreneurSupport() {
		return null == entrepreneurSupport ? Boolean.FALSE : entrepreneurSupport;
	}

	public void setEntrepreneurSupport(Boolean entrepreneurSupport) {
		this.entrepreneurSupport = entrepreneurSupport;
	}

	public String getBonusColective() {
		return null == bonusColective ? "" : bonusColective;
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
		return null == promotionMeasures ? Boolean.FALSE : promotionMeasures;
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
		return null == quoteReductions ? Boolean.FALSE : quoteReductions;
	}

	public void setQuoteReductions(Boolean quoteReductions) {
		this.quoteReductions = quoteReductions;
	}

	public String getReductionColective() {
		return null == reductionColective ? "" : reductionColective;
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

	public String getJourneyPercent() {
		return journeyPercent;
	}

	public void setJourneyPercent(String journeyPercentTB) {
		this.journeyPercent = journeyPercentTB;
	}
	
	public Boolean getOlderThan52() {
		return null == olderThan52 ? Boolean.FALSE : olderThan52;
	}

	public void setOlderThan52(Boolean olderThan52) {
		this.olderThan52 = olderThan52;
	}

	public String getOtherLegislations() {
		return null == otherLegislations ? "" : otherLegislations;
	}

	public void setOtherLegislations(String otherLegislations) {
		this.otherLegislations = otherLegislations;
	}
	
	public Boolean getBonus() {
		return null == bonus ? Boolean.FALSE : bonus;
	}

	public void setBonus(Boolean bonus) {
		this.bonus = bonus;
	}

	public String getBonusType() {
		return null == bonusType ? "" : bonusType;
	}

	public void setBonusType(String bonusType) {
		this.bonusType = bonusType;
	}

	public Boolean getDisc() {
		return disc;
	}

	public void setDisc(Boolean disc) {
		this.disc = disc;
	}

	public String getDiscReason() {
		return discReason;
	}

	public void setDiscReason(String discReason) {
		this.discReason = discReason;
	}
	
	public Boolean getTrueDate() {
		return trueDate;
	}

	public void setTrueDate(Boolean trueDate) {
		this.trueDate = trueDate;
	}

	public Boolean getPlanRecovery() {
		return null == planRecovery ? Boolean.FALSE : planRecovery;
	}

	public void setPlanRecovery(Boolean planRecovery) {
		this.planRecovery = planRecovery;
	}
	
}
