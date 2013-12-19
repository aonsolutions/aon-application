package com.code.aon.file.tax.model.MOD190;

import com.code.aon.file.tax.FileTaxUtil;

public class Receiver {

	private String document;
	private String representativeDocument;
	private String name;
	private int province;
	private String key;
	private String subKey;
	private double perception;
	private double retention;
	private double inKindPerception;
	private double inKindDeposit;
	private double inKindOutputDeposit;
	private int accrualYear;
	private int ceutaMelilla;
	private int birthYear;
	private int familySituation;
	private String spouseDocument;
	private int disability;
	private int contract;
	private int workActivityExtension;
	private int geographicMobility;
	private double applicableReduction;
	private double deducibleExpense;
	private double compensatoryPension;
	private double foodAnnuality;
	private int homeLoanCommunnication;
	private int lessThan3Descendent;
	private int lessThan3DescendentRatio;
	private int otherDescendent;
	private int otherDescendentRatio;
	private int disabilityDescendent33;
	private int disabilityDescendent33Ratio;
	private int disabilityDescendentDependence;
	private int disabilityDescendentDependenceRatio;
	private int disabilityDescendent65;
	private int disabilityDescendent65Ratio;
	private int lessThan75Ascendant;
	private int lessThan75AscendantRatio;
	private int ascendant;
	private int ascendantRatio;
	private int disabilityAscendant33;
	private int disabilityAscendant33Ratio;
	private int disabilityAscendantDependence;
	private int disabilityAscendantDependenceRatio;
	private int disabilityAscendant65;
	private int disabilityAscendant65Ratio;
	private int firstChildCalculation;
	private int secondChildCalculation;
	private int thirdChildCalculation;
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = FileTaxUtil.changeInvalidCharacters(document);
	}
	public String getRepresentativeDocument() {
		return representativeDocument;
	}
	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = FileTaxUtil.changeInvalidCharacters(representativeDocument);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = FileTaxUtil.changeInvalidCharacters(name);
	}
	public int getProvince() {
		return province;
	}
	public void setProvince(int province) {
		this.province = province;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSubKey() {
		return subKey;
	}
	public void setSubKey(String subKey) {
		this.subKey = subKey;
	}
	public double getPerception() {
		return perception;
	}
	public void setPerception(double perception) {
		this.perception = perception;
	}
	public double getRetention() {
		return retention;
	}
	public void setRetention(double retention) {
		this.retention = retention;
	}
	public double getInKindPerception() {
		return inKindPerception;
	}
	public void setInKindPerception(double inKindPerception) {
		this.inKindPerception = inKindPerception;
	}
	public double getInKindDeposit() {
		return inKindDeposit;
	}
	public void setInKindDeposit(double inKindDeposit) {
		this.inKindDeposit = inKindDeposit;
	}
	public double getInKindOutputDeposit() {
		return inKindOutputDeposit;
	}
	public void setInKindOutputDeposit(double inKindOutputDeposit) {
		this.inKindOutputDeposit = inKindOutputDeposit;
	}
	public int getAccrualYear() {
		return accrualYear;
	}
	public void setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
	}
	public int getCeutaMelilla() {
		return ceutaMelilla;
	}
	public void setCeutaMelilla(int ceutaMelilla) {
		this.ceutaMelilla = ceutaMelilla;
	}
	public int getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	public int getFamilySituation() {
		return familySituation;
	}
	public void setFamilySituation(int familySituation) {
		this.familySituation = familySituation;
	}
	public String getSpouseDocument() {
		return spouseDocument;
	}
	public void setSpouseDocument(String spouseDocument) {
		this.spouseDocument = FileTaxUtil.changeInvalidCharacters(spouseDocument);
	}
	public int getDisability() {
		return disability;
	}
	public void setDisability(int disability) {
		this.disability = disability;
	}
	public int getContract() {
		return contract;
	}
	public void setContract(int contract) {
		this.contract = contract;
	}
	public int getWorkActivityExtension() {
		return workActivityExtension;
	}
	public void setWorkActivityExtension(int workActivityExtension) {
		this.workActivityExtension = workActivityExtension;
	}
	public int getGeographicMobility() {
		return geographicMobility;
	}
	public void setGeographicMobility(int geographicMobility) {
		this.geographicMobility = geographicMobility;
	}
	public double getApplicableReduction() {
		return applicableReduction;
	}
	public void setApplicableReduction(double applicableReduction) {
		this.applicableReduction = applicableReduction;
	}
	public double getDeducibleExpense() {
		return deducibleExpense;
	}
	public void setDeducibleExpense(double deducibleExpense) {
		this.deducibleExpense = deducibleExpense;
	}
	public double getCompensatoryPension() {
		return compensatoryPension;
	}
	public void setCompensatoryPension(double compensatoryPension) {
		this.compensatoryPension = compensatoryPension;
	}
	public double getFoodAnnuality() {
		return foodAnnuality;
	}
	public void setFoodAnnuality(double foodAnnuality) {
		this.foodAnnuality = foodAnnuality;
	}
	public int getHomeLoanCommunnication() {
		return homeLoanCommunnication;
	}
	public void setHomeLoanCommunnication(int homeLoanCommunnication) {
		this.homeLoanCommunnication = homeLoanCommunnication;
	}
	public int getLessThan3Descendent() {
		return lessThan3Descendent;
	}
	public void setLessThan3Descendent(int lessThan3Descendent) {
		this.lessThan3Descendent = lessThan3Descendent;
	}
	public int getLessThan3DescendentRatio() {
		return lessThan3DescendentRatio;
	}
	public void setLessThan3DescendentRatio(int lessThan3DescendentRatio) {
		this.lessThan3DescendentRatio = lessThan3DescendentRatio;
	}
	public int getOtherDescendent() {
		return otherDescendent;
	}
	public void setOtherDescendent(int otherDescendent) {
		this.otherDescendent = otherDescendent;
	}
	public int getOtherDescendentRatio() {
		return otherDescendentRatio;
	}
	public void setOtherDescendentRatio(int otherDescendentRatio) {
		this.otherDescendentRatio = otherDescendentRatio;
	}
	public int getDisabilityDescendent33() {
		return disabilityDescendent33;
	}
	public void setDisabilityDescendent33(int disabilityDescendent33) {
		this.disabilityDescendent33 = disabilityDescendent33;
	}
	public int getDisabilityDescendent33Ratio() {
		return disabilityDescendent33Ratio;
	}
	public void setDisabilityDescendent33Ratio(int disabilityDescendent33Ratio) {
		this.disabilityDescendent33Ratio = disabilityDescendent33Ratio;
	}
	public int getDisabilityDescendentDependence() {
		return disabilityDescendentDependence;
	}
	public void setDisabilityDescendentDependence(int disabilityDescendentDependence) {
		this.disabilityDescendentDependence = disabilityDescendentDependence;
	}
	public int getDisabilityDescendentDependenceRatio() {
		return disabilityDescendentDependenceRatio;
	}
	public void setDisabilityDescendentDependenceRatio(
			int disabilityDescendentDependenceRatio) {
		this.disabilityDescendentDependenceRatio = disabilityDescendentDependenceRatio;
	}
	public int getDisabilityDescendent65() {
		return disabilityDescendent65;
	}
	public void setDisabilityDescendent65(int disabilityDescendent65) {
		this.disabilityDescendent65 = disabilityDescendent65;
	}
	public int getDisabilityDescendent65Ratio() {
		return disabilityDescendent65Ratio;
	}
	public void setDisabilityDescendent65Ratio(int disabilityDescendent65Ratio) {
		this.disabilityDescendent65Ratio = disabilityDescendent65Ratio;
	}
	public int getLessThan75Ascendant() {
		return lessThan75Ascendant;
	}
	public void setLessThan75Ascendant(int lessThan75Ascendant) {
		this.lessThan75Ascendant = lessThan75Ascendant;
	}
	public int getLessThan75AscendantRatio() {
		return lessThan75AscendantRatio;
	}
	public void setLessThan75AscendantRatio(int lessThan75AscendantRatio) {
		this.lessThan75AscendantRatio = lessThan75AscendantRatio;
	}
	public int getAscendant() {
		return ascendant;
	}
	public void setAscendant(int ascendant) {
		this.ascendant = ascendant;
	}
	public int getAscendantRatio() {
		return ascendantRatio;
	}
	public void setAscendantRatio(int ascendantRatio) {
		this.ascendantRatio = ascendantRatio;
	}
	public int getDisabilityAscendant33() {
		return disabilityAscendant33;
	}
	public void setDisabilityAscendant33(int disabilityAscendant33) {
		this.disabilityAscendant33 = disabilityAscendant33;
	}
	public int getDisabilityAscendant33Ratio() {
		return disabilityAscendant33Ratio;
	}
	public void setDisabilityAscendant33Ratio(int disabilityAscendant33Ratio) {
		this.disabilityAscendant33Ratio = disabilityAscendant33Ratio;
	}
	public int getDisabilityAscendantDependence() {
		return disabilityAscendantDependence;
	}
	public void setDisabilityAscendantDependence(int disabilityAscendantDependence) {
		this.disabilityAscendantDependence = disabilityAscendantDependence;
	}
	public int getDisabilityAscendantDependenceRatio() {
		return disabilityAscendantDependenceRatio;
	}
	public void setDisabilityAscendantDependenceRatio(
			int disabilityAscendantDependenceRatio) {
		this.disabilityAscendantDependenceRatio = disabilityAscendantDependenceRatio;
	}
	public int getDisabilityAscendant65() {
		return disabilityAscendant65;
	}
	public void setDisabilityAscendant65(int disabilityAscendant65) {
		this.disabilityAscendant65 = disabilityAscendant65;
	}
	public int getDisabilityAscendant65Ratio() {
		return disabilityAscendant65Ratio;
	}
	public void setDisabilityAscendant65Ratio(int disabilityAscendant65Ratio) {
		this.disabilityAscendant65Ratio = disabilityAscendant65Ratio;
	}
	public int getFirstChildCalculation() {
		return firstChildCalculation;
	}
	public void setFirstChildCalculation(int firstChildCalculation) {
		this.firstChildCalculation = firstChildCalculation;
	}
	public int getSecondChildCalculation() {
		return secondChildCalculation;
	}
	public void setSecondChildCalculation(int secondChildCalculation) {
		this.secondChildCalculation = secondChildCalculation;
	}
	public int getThirdChildCalculation() {
		return thirdChildCalculation;
	}
	public void setThirdChildCalculation(int thirdChildCalculation) {
		this.thirdChildCalculation = thirdChildCalculation;
	}
	
}
