package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod190Detail implements Serializable {

	private static final long serialVersionUID = 7374076019343100903L;
	
	private Integer id;
	private String name;
	private int domain;
	private int mod190;

	private String document;
	private String representativeDocument;
	private int province;
	private String key;
	private String subKey;

	private double perception;
	private double retention;
	private double inKindPerception;
	private double inKindDeposit;
	private double inKindOutputDeposit;
	private int accrualYear;

	private int tempId;

	private boolean ceutaMelilla;
	private int birthYear;
	private byte familySituation;
	private String spouseDocument;
	private byte disability;
	private byte contract;
	private boolean workActivityExtension;
	private boolean geographicMobility;

	private double perceptionIL;
	private double retentionIL;
	private double outputRetentionIL;
	
	private double inKindPerceptionIL;
	private double inKindDepositIL;
    private double inKindOutputDepositIL;

	private double applicableReduction;
	private double deducibleExpense;
	private double compensatoryPension;
	private double foodAnnuality;

	private boolean homeLoanCommunnication;

	private byte lessThan3Descendent;
	private byte lessThan3DescendentRatio;
	private byte otherDescendent;
	private byte otherDescendentRatio;

	private byte disabilityDescendent33;
	private byte disabilityDescendent33Ratio;
	private byte disabilityDescendentDependence;
	private byte disabilityDescendentDependenceRatio;
	private byte disabilityDescendent65;
	private byte disabilityDescendent65Ratio;

	private byte lessThan75Ascendant;
	private byte lessThan75AscendantRatio;
	private byte ascendant;
	private byte ascendantRatio;

	private byte disabilityAscendant33;
	private byte disabilityAscendant33Ratio;
	private byte disabilityAscendantDependence;
	private byte disabilityAscendantDependenceRatio;
	private byte disabilityAscendant65;
	private byte disabilityAscendant65Ratio;

	private byte firstChildCalculation;
	private byte secondChildCalculation;
	private byte thirdChildCalculation;
	
	private byte titConvivencia;
	private byte compInfancia;

	private boolean dirty;
	private boolean deleted;
	
	private double salaryPerception;
	private double salaryRetention;

	public Integer getId() {
		return id;
	}

	public Mod190Detail setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod190Detail setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod190Detail setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod190() {
		return mod190;
	}

	public Mod190Detail setMod190(int mod190) {
		this.mod190 = mod190;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod190Detail setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod190Detail setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}

	public int getProvince() {
		return province;
	}

	public Mod190Detail setProvince(int province) {
		this.province = province;
		return this;
	}

	public String getKey() {
		return key;
	}

	public Mod190Detail setKey(String key) {
		this.key = key;
		return this;
	}

	public String getSubKey() {
		return subKey;
	}

	public Mod190Detail setSubKey(String subKey) {
		this.subKey = subKey;
		return this;
	}

	public double getPerception() {
		return perception;
	}

	public Mod190Detail setPerception(double perception) {
		this.perception = perception;
		return this;
	}

	public double getRetention() {
		return retention;
	}

	public Mod190Detail setRetention(double retention) {
		this.retention = retention;
		return this;
	}

	public double getInKindPerception() {
		return inKindPerception;
	}

	public Mod190Detail setInKindPerception(double inKindPerception) {
		this.inKindPerception = inKindPerception;
		return this;
	}

	public double getInKindDeposit() {
		return inKindDeposit;
	}

	public Mod190Detail setInKindDeposit(double inKindDeposit) {
		this.inKindDeposit = inKindDeposit;
		return this;
	}

	public double getInKindOutputDeposit() {
		return inKindOutputDeposit;
	}

	public Mod190Detail setInKindOutputDeposit(double inKindOutputDeposit) {
		this.inKindOutputDeposit = inKindOutputDeposit;
		return this;
	}

	public int getAccrualYear() {
		return accrualYear;
	}

	public Mod190Detail setAccrualYear(int accrualYear) {
		this.accrualYear = accrualYear;
		return this;
	}

	public double getPerceptionIL() {
		return perceptionIL;
	}
	public Mod190Detail setPerceptionIL(double perceptionIL) {
		this.perceptionIL = perceptionIL;
		return this;
	}

	public double getRetentionIL() {
		return retentionIL;
	}
	public Mod190Detail setRetentionIL(double retentionIL) {
		this.retentionIL = retentionIL;
		return this;
	}
	
	public double getOutputRetentionIL() {
		return outputRetentionIL;
	}
	public Mod190Detail setOutputRetentionIL(double outputRetentionIL) {
		this.outputRetentionIL = outputRetentionIL;
		return this;
	}

	public double getInKindPerceptionIL() {
		return inKindPerceptionIL;
	}
	public Mod190Detail setInKindPerceptionIL(double inKindPerceptionIL) {
		this.inKindPerceptionIL = inKindPerceptionIL;
		return this;
	}

	public double getInKindDepositIL() {
		return inKindDepositIL;
	}
	public Mod190Detail setInKindDepositIL(double inKindDepositIL) {
		this.inKindDepositIL = inKindDepositIL;
		return this;
	}

	public double getInKindOutputDepositIL() {
		return inKindOutputDepositIL;
	}
	public Mod190Detail setInKindOutputDepositIL(double inKindOutputDepositIL) {
		this.inKindOutputDepositIL = inKindOutputDepositIL;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod190Detail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod190Detail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod190Detail setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	public boolean isCeutaMelilla() {
		return ceutaMelilla;
	}

	public Mod190Detail setCeutaMelilla(boolean ceutaMelilla) {
		this.ceutaMelilla = ceutaMelilla;
		return this;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public Mod190Detail setBirthYear(int birthYear) {
		this.birthYear = birthYear;
		return this;
	}

	public byte getFamilySituation() {
		return familySituation;
	}

	public Mod190Detail setFamilySituation(byte familySituation) {
		this.familySituation = familySituation;
		return this;
	}

	public String getSpouseDocument() {
		return spouseDocument;
	}

	public Mod190Detail setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
		return this;
	}

	public byte getDisability() {
		return disability;
	}

	public Mod190Detail setDisability(byte disability) {
		this.disability = disability;
		return this;
	}

	public byte getContract() {
		return contract;
	}

	public Mod190Detail setContract(byte contract) {
		this.contract = contract;
		return this;
	}

	public boolean isWorkActivityExtension() {
		return workActivityExtension;
	}

	public Mod190Detail setWorkActivityExtension(boolean workActivityExtension) {
		this.workActivityExtension = workActivityExtension;
		return this;
	}

	public boolean isGeographicMobility() {
		return geographicMobility;
	}

	public Mod190Detail setGeographicMobility(boolean geographicMobility) {
		this.geographicMobility = geographicMobility;
		return this;
	}

	public double getApplicableReduction() {
		return applicableReduction;
	}
	public Mod190Detail setApplicableReduction(double applicableReduction) {
		this.applicableReduction = applicableReduction;
		return this;
	}
	public double getDeducibleExpense() {
		return deducibleExpense;
	}
	public Mod190Detail setDeducibleExpense(double deducibleExpense) {
		this.deducibleExpense = deducibleExpense;
		return this;
	}
	public double getCompensatoryPension() {
		return compensatoryPension;
	}
	public Mod190Detail setCompensatoryPension(double compensatoryPension) {
		this.compensatoryPension = compensatoryPension;
		return this;
	}
	public double getFoodAnnuality() {
		return foodAnnuality;
	}
	public Mod190Detail setFoodAnnuality(double foodAnnuality) {
		this.foodAnnuality = foodAnnuality;
		return this;
	}
	public boolean isHomeLoanCommunnication() {
		return homeLoanCommunnication;
	}
	public Mod190Detail setHomeLoanCommunnication(boolean homeLoanCommunnication) {
		this.homeLoanCommunnication = homeLoanCommunnication;
		return this;
	}
	public byte getLessThan3Descendent() {
		return lessThan3Descendent;
	}
	public Mod190Detail setLessThan3Descendent(byte lessThan3Descendent) {
		this.lessThan3Descendent = lessThan3Descendent;
		return this;
	}
	public byte getLessThan3DescendentRatio() {
		return lessThan3DescendentRatio;
	}
	public Mod190Detail setLessThan3DescendentRatio(byte lessThan3DescendentRatio) {
		this.lessThan3DescendentRatio = lessThan3DescendentRatio;
		return this;
	}
	public byte getOtherDescendent() {
		return otherDescendent;
	}
	public Mod190Detail setOtherDescendent(byte otherDescendent) {
		this.otherDescendent = otherDescendent;
		return this;
	}
	public byte getOtherDescendentRatio() {
		return otherDescendentRatio;
	}
	public Mod190Detail setOtherDescendentRatio(byte otherDescendentRatio) {
		this.otherDescendentRatio = otherDescendentRatio;
		return this;
	}
	public byte getDisabilityDescendent33() {
		return disabilityDescendent33;
	}
	public Mod190Detail setDisabilityDescendent33(byte disabilityDescendent33) {
		this.disabilityDescendent33 = disabilityDescendent33;
		return this;
	}
	public byte getDisabilityDescendent33Ratio() {
		return disabilityDescendent33Ratio;
	}
	public Mod190Detail setDisabilityDescendent33Ratio(byte disabilityDescendent33Ratio) {
		this.disabilityDescendent33Ratio = disabilityDescendent33Ratio;
		return this;
	}
	public byte getDisabilityDescendentDependence() {
		return disabilityDescendentDependence;
	}
	public Mod190Detail setDisabilityDescendentDependence(byte disabilityDescendentDependence) {
		this.disabilityDescendentDependence = disabilityDescendentDependence;
		return this;
	}
	public byte getDisabilityDescendentDependenceRatio() {
		return disabilityDescendentDependenceRatio;
	}
	public Mod190Detail setDisabilityDescendentDependenceRatio(
			byte disabilityDescendentDependenceRatio) {
		this.disabilityDescendentDependenceRatio = disabilityDescendentDependenceRatio;
		return this;
	}
	public byte getDisabilityDescendent65() {
		return disabilityDescendent65;
	}
	public Mod190Detail setDisabilityDescendent65(byte disabilityDescendent65) {
		this.disabilityDescendent65 = disabilityDescendent65;
		return this;
	}
	public byte getDisabilityDescendent65Ratio() {
		return disabilityDescendent65Ratio;
	}
	public Mod190Detail setDisabilityDescendent65Ratio(byte disabilityDescendent65Ratio) {
		this.disabilityDescendent65Ratio = disabilityDescendent65Ratio;
		return this;
	}
	public byte getLessThan75Ascendant() {
		return lessThan75Ascendant;
	}
	public Mod190Detail setLessThan75Ascendant(byte lessThan75Ascendant) {
		this.lessThan75Ascendant = lessThan75Ascendant;
		return this;
	}
	public byte getLessThan75AscendantRatio() {
		return lessThan75AscendantRatio;
	}
	public Mod190Detail setLessThan75AscendantRatio(byte lessThan75AscendantRatio) {
		this.lessThan75AscendantRatio = lessThan75AscendantRatio;
		return this;
	}
	public byte getAscendant() {
		return ascendant;
	}
	public Mod190Detail setAscendant(byte ascendant) {
		this.ascendant = ascendant;
		return this;
	}
	public byte getAscendantRatio() {
		return ascendantRatio;
	}
	public Mod190Detail setAscendantRatio(byte ascendantRatio) {
		this.ascendantRatio = ascendantRatio;
		return this;
	}
	public byte getDisabilityAscendant33() {
		return disabilityAscendant33;
	}
	public Mod190Detail setDisabilityAscendant33(byte disabilityAscendant33) {
		this.disabilityAscendant33 = disabilityAscendant33;
		return this;
	}
	public byte getDisabilityAscendant33Ratio() {
		return disabilityAscendant33Ratio;
	}
	public Mod190Detail setDisabilityAscendant33Ratio(byte disabilityAscendant33Ratio) {
		this.disabilityAscendant33Ratio = disabilityAscendant33Ratio;
		return this;
	}
	public byte getDisabilityAscendantDependence() {
		return disabilityAscendantDependence;
	}
	public Mod190Detail setDisabilityAscendantDependence(byte disabilityAscendantDependence) {
		this.disabilityAscendantDependence = disabilityAscendantDependence;
		return this;
	}
	public byte getDisabilityAscendantDependenceRatio() {
		return disabilityAscendantDependenceRatio;
	}
	public Mod190Detail setDisabilityAscendantDependenceRatio(
			byte disabilityAscendantDependenceRatio) {
		this.disabilityAscendantDependenceRatio = disabilityAscendantDependenceRatio;
		return this;
	}
	public byte getDisabilityAscendant65() {
		return disabilityAscendant65;
	}
	public Mod190Detail setDisabilityAscendant65(byte disabilityAscendant65) {
		this.disabilityAscendant65 = disabilityAscendant65;
		return this;
	}
	public byte getDisabilityAscendant65Ratio() {
		return disabilityAscendant65Ratio;
	}
	public Mod190Detail setDisabilityAscendant65Ratio(byte disabilityAscendant65Ratio) {
		this.disabilityAscendant65Ratio = disabilityAscendant65Ratio;
		return this;
	}
	public byte getFirstChildCalculation() {
		return firstChildCalculation;
	}
	public Mod190Detail setFirstChildCalculation(byte firstChildCalculation) {
		this.firstChildCalculation = firstChildCalculation;
		return this;
	}
	public byte getSecondChildCalculation() {
		return secondChildCalculation;
	}
	public Mod190Detail setSecondChildCalculation(byte secondChildCalculation) {
		this.secondChildCalculation = secondChildCalculation;
		return this;
	}
	public byte getThirdChildCalculation() {
		return thirdChildCalculation;
	}
	public Mod190Detail setThirdChildCalculation(byte thirdChildCalculation) {
		this.thirdChildCalculation = thirdChildCalculation;
		return this;
	}
	public byte getTitConvivencia() {
		return titConvivencia;
	}
	public Mod190Detail setTitConvivencia(byte titConvivencia) {
		this.titConvivencia = titConvivencia;
		return this;
	}
	public byte getCompInfancia() {
		return compInfancia;
	}
	public Mod190Detail setCompInfancia(byte compInfancia) {
		this.compInfancia = compInfancia;
		return this;
	}
	public double getSalaryPerception() {
		return salaryPerception;
	}
	public Mod190Detail setSalaryPerception(double salaryPerception) {
		this.salaryPerception = salaryPerception;
		return this;
	}

	public double getSalaryRetention() {
		return salaryRetention;
	}
	public Mod190Detail setSalaryRetention(double salaryRetention) {
		this.salaryRetention = salaryRetention;
		return this;
	}


}
