package com.esferalia.aon.ui.payroll.controller.wizard;

import java.util.Date;

import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.BasicCopySignatureType;
import com.esferalia.aon.payroll.enumeration.DisabilityCode;
import com.esferalia.aon.payroll.enumeration.DismissalCollective;
import com.esferalia.aon.payroll.enumeration.EducationalLevel;
import com.esferalia.aon.payroll.enumeration.EmployeeType;
import com.esferalia.aon.payroll.enumeration.EmploymentProgram;
import com.esferalia.aon.payroll.enumeration.OtherLaws;
import com.esferalia.aon.payroll.enumeration.SchoolWorkshop;

public class ContrataParams {
	
	/* 
	 * generales de contrato 
	 */
	private EducationalLevel educationalLevel;
	private DisabilityCode disabilityCode; 
	private boolean collectiveAgreement;
	private Double timeUnit;
	private String profession;
	private String offer;
	private String campaign;
	private CNO cno;
	private EmploymentProgram employmentProgram;
	private OtherLaws otherLaws;
	private String signaturePlace;
	private Date signatureDate;
	/* 
	 * datos de contrata 
	 */
//	private TimeType timeType;
	private String timeType;
	private boolean theoryTraining;
//	private AgeGroup ageGroup;
	private String ageGroup;
	private Double partialRetirement;
	private boolean nonDateActivity;
	private boolean periodicalDiscontinuous;
	private SchoolWorkshop schoolWorkshop;
//	private EmployerType employerType;
	private String employerType;
//	private InvestigationJobType investigationJobType;
	private String investigationJobType;
	private boolean investigationJobRD;
//	private LocalCorporation localCorporation;
	private String localCorporation;
//	private Actuation actuation;
	private String actuation;
	private Double financialYear;
	private BasicCopySignatureType basicCopySignatureType;
	private String basicCopyComments;
	private String ettCif;
	private String ettName;
	private boolean ettContractTemplate;
	private boolean ettForeignEnterprise;
	private boolean permanentContractDevelopment;
	private DismissalCollective dismissalCollective;
	private EmployeeType reliefEmployeeType;
	private Person reliefPerson;
	
	// checks datos especificos contrato
	private boolean employmentProgramData;
	private boolean ettData;
	private boolean reliefData;
	private boolean offerData;
	private boolean schoolWorkshopData;
	private boolean disabilityData;
	private boolean olderThan52Data;
	private boolean annexData;
	private boolean canpaignData;
	
	
	
	
	public boolean isEmploymentProgramData() {
		return employmentProgramData;
	}
	public void setEmploymentProgramData(boolean employmentProgramData) {
		this.employmentProgramData = employmentProgramData;
	}
	public boolean isEttData() {
		return ettData;
	}
	public void setEttData(boolean ettData) {
		this.ettData = ettData;
	}
	public boolean isReliefData() {
		return reliefData;
	}
	public void setReliefData(boolean reliefData) {
		this.reliefData = reliefData;
	}
	public boolean isOfferData() {
		return offerData;
	}
	public void setOfferData(boolean offerData) {
		this.offerData = offerData;
	}
	public boolean isSchoolWorkshopData() {
		return schoolWorkshopData;
	}
	public void setSchoolWorkshopData(boolean schoolWorkshopData) {
		this.schoolWorkshopData = schoolWorkshopData;
	}
	public boolean isDisabilityData() {
		return disabilityData;
	}
	public void setDisabilityData(boolean disabilityData) {
		this.disabilityData = disabilityData;
	}
	public boolean isOlderThan52Data() {
		return olderThan52Data;
	}
	public void setOlderThan52Data(boolean olderThan52Data) {
		this.olderThan52Data = olderThan52Data;
	}
	public boolean isAnnexData() {
		return annexData;
	}
	public void setAnnexData(boolean annexData) {
		this.annexData = annexData;
	}
	public boolean isCanpaignData() {
		return canpaignData;
	}
	public void setCanpaignData(boolean canpaignData) {
		this.canpaignData = canpaignData;
	}
	public EmployeeType getReliefEmployeeType() {
		return reliefEmployeeType;
	}
	public void setReliefEmployeeType(EmployeeType reliefEmployeeType) {
		this.reliefEmployeeType = reliefEmployeeType;
	}
	public Person getReliefPerson() {
		if(reliefPerson==null){
			reliefPerson = new Person();
		}
		return reliefPerson;
	}
	public void setReliefPerson(Person reliefPerson) {
		this.reliefPerson = reliefPerson;
	}
	public DismissalCollective getDismissalCollective() {
		return dismissalCollective;
	}
	public void setDismissalCollective(DismissalCollective dismissalCollective) {
		this.dismissalCollective = dismissalCollective;
	}
	public boolean isPermanentContractDevelopment() {
		return permanentContractDevelopment;
	}
	public void setPermanentContractDevelopment(boolean permanentContractDevelopment) {
		this.permanentContractDevelopment = permanentContractDevelopment;
	}
	public EducationalLevel getEducationalLevel() {
		return educationalLevel;
	}
	public void setEducationalLevel(EducationalLevel educationalLevel) {
		this.educationalLevel = educationalLevel;
	}
	public DisabilityCode getDisabilityCode() {
		return disabilityCode;
	}
	public void setDisabilityCode(DisabilityCode disabilityCode) {
		this.disabilityCode = disabilityCode;
	}
	public boolean isCollectiveAgreement() {
		return collectiveAgreement;
	}
	public void setCollectiveAgreement(boolean collectiveAgreement) {
		this.collectiveAgreement = collectiveAgreement;
	}
	public Double getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(Double timeUnit) {
		this.timeUnit = timeUnit;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getOffer() {
		return offer;
	}
	public void setOffer(String offer) {
		this.offer = offer;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public CNO getCno() {
		if(cno==null){
			cno = new CNO();
		}
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
	public EmploymentProgram getEmploymentProgram() {
		return employmentProgram;
	}
	public void setEmploymentProgram(EmploymentProgram employmentProgram) {
		this.employmentProgram = employmentProgram;
	}
	public OtherLaws getOtherLaws() {
		return otherLaws;
	}
	public void setOtherLaws(OtherLaws otherLaws) {
		this.otherLaws = otherLaws;
	}
	public String getSignaturePlace() {
		return signaturePlace;
	}
	public void setSignaturePlace(String signaturePlace) {
		this.signaturePlace = signaturePlace;
	}
	public Date getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	public String getTimeType() {
		return timeType;
	}
	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
	public boolean isTheoryTraining() {
		return theoryTraining;
	}
	public void setTheoryTraining(boolean theoryTraining) {
		this.theoryTraining = theoryTraining;
	}
	public String getAgeGroup() {
		return ageGroup;
	}
	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}
	public Double getPartialRetirement() {
		return partialRetirement;
	}
	public void setPartialRetirement(Double partialRetirement) {
		this.partialRetirement = partialRetirement;
	}
	public boolean isNonDateActivity() {
		return nonDateActivity;
	}
	public void setNonDateActivity(boolean nonDateActivity) {
		this.nonDateActivity = nonDateActivity;
	}
	public boolean isPeriodicalDiscontinuous() {
		return periodicalDiscontinuous;
	}
	public void setPeriodicalDiscontinuous(boolean periodicalDiscontinuous) {
		this.periodicalDiscontinuous = periodicalDiscontinuous;
	}
	public SchoolWorkshop getSchoolWorkshop() {
		return schoolWorkshop;
	}
	public void setSchoolWorkshop(SchoolWorkshop schoolWorkshop) {
		this.schoolWorkshop = schoolWorkshop;
	}
	public String getEmployerType() {
		return employerType;
	}
	public void setEmployerType(String employerType) {
		this.employerType = employerType;
	}
	public String getInvestigationJobType() {
		return investigationJobType;
	}
	public void setInvestigationJobType(String investigationJobType) {
		this.investigationJobType = investigationJobType;
	}
	public boolean isInvestigationJobRD() {
		return investigationJobRD;
	}
	public void setInvestigationJobRD(boolean investigationJobRD) {
		this.investigationJobRD = investigationJobRD;
	}
	public String getLocalCorporation() {
		return localCorporation;
	}
	public void setLocalCorporation(String localCorporation) {
		this.localCorporation = localCorporation;
	}
	public String getActuation() {
		return actuation;
	}
	public void setActuation(String actuation) {
		this.actuation = actuation;
	}
	public Double getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(Double financialYear) {
		this.financialYear = financialYear;
	}
	public BasicCopySignatureType getBasicCopySignatureType() {
		return basicCopySignatureType;
	}
	public void setBasicCopySignatureType(BasicCopySignatureType basicCopySignatureType) {
		this.basicCopySignatureType = basicCopySignatureType;
	}
	public String getBasicCopyComments() {
		return basicCopyComments;
	}
	public void setBasicCopyComments(String basicCopyComments) {
		this.basicCopyComments = basicCopyComments;
	}
	public String getEttCif() {
		return ettCif;
	}
	public void setEttCif(String ettCif) {
		this.ettCif = ettCif;
	}
	public String getEttName() {
		return ettName;
	}
	public void setEttName(String ettName) {
		this.ettName = ettName;
	}
	public boolean isEttContractTemplate() {
		return ettContractTemplate;
	}
	public void setEttContractTemplate(boolean ettContractTemplate) {
		this.ettContractTemplate = ettContractTemplate;
	}
	public boolean isEttForeignEnterprise() {
		return ettForeignEnterprise;
	}
	public void setEttForeignEnterprise(boolean ettForeignEnterprise) {
		this.ettForeignEnterprise = ettForeignEnterprise;
	}
	
	
	
	
}
