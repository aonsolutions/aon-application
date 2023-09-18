package com.esferalia.aon.occam.api.model.mod145;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Mod145 implements Serializable {

	// References to IrpfData table
	
	private static final long serialVersionUID = -1747235992732974374L;

	private Integer id;
	private Integer domain;
	private String enterpriseName;
	private Integer contract;
	private String nif;
	private String fullName;
	private Date birthDate;
	private Byte familySituation;
	private String spouseDocument;
	private Byte disabilityLevel;
	private boolean dependence;
	private Date movingDate;
	private boolean labourProlongation;
	private Byte descendientCount;
	private Date startDate;
	private Date endDate;
	private boolean fiscalExclusion;
	private Date issueDate;
	private Double spousalSupport;
	private Double foodAnnuity;
	private Double irpfPercent;
	private boolean deductionHomeLoan;
	private boolean ceutaMelillaPalma;
	
	private List<IrpfDataAscendants> ascendants;
	private List<IrpfDataDescendients> descendients;
	
	private boolean deleted;
	
	public Mod145() {
		super();
		this.ascendants = new ArrayList<>();
		this.descendients = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}

	public Mod145 setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Mod145 setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public Mod145 setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public Integer getContract() {
		return contract;
	}

	public Mod145 setContract(Integer contract) {
		this.contract = contract;
		return this;
	}

	public String getNif() {
		return nif;
	}

	public Mod145 setNif(String nif) {
		this.nif = nif;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public Mod145 setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public Mod145 setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
		return this;
	}

	public Byte getFamilySituation() {
		return familySituation;
	}

	public Mod145 setFamilySituation(Byte familySituation) {
		this.familySituation = familySituation;
		return this;
	}

	public String getSpouseDocument() {
		return spouseDocument;
	}

	public Mod145 setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
		return this;
	}

	public Byte getDisabilityLevel() {
		return disabilityLevel;
	}

	public Mod145 setDisabilityLevel(Byte disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
		return this;
	}

	public boolean isDependence() {
		return dependence;
	}

	public Mod145 setDependence(boolean dependence) {
		this.dependence = dependence;
		return this;
	}

	public Date getMovingDate() {
		return movingDate;
	}

	public Mod145 setMovingDate(Date movingDate) {
		this.movingDate = movingDate;
		return this;
	}

	public boolean isLabourProlongation() {
		return labourProlongation;
	}

	public Mod145 setLabourProlongation(boolean labourProlongation) {
		this.labourProlongation = labourProlongation;
		return this;
	}

	public Byte getDescendientCount() {
		return descendientCount;
	}

	public Mod145 setDescendientCount(Byte descendientCount) {
		this.descendientCount = descendientCount;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Mod145 setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Mod145 setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public boolean isFiscalExclusion() {
		return fiscalExclusion;
	}

	public Mod145 setFiscalExclusion(boolean fiscalExclusion) {
		this.fiscalExclusion = fiscalExclusion;
		return this;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public Mod145 setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public Double getSpousalSupport() {
		return spousalSupport;
	}

	public Mod145 setSpousalSupport(Double spousalSupport) {
		this.spousalSupport = spousalSupport;
		return this;
	}

	public Double getFoodAnnuity() {
		return foodAnnuity;
	}

	public Mod145 setFoodAnnuity(Double foodAnnuity) {
		this.foodAnnuity = foodAnnuity;
		return this;
	}

	public Double getIrpfPercent() {
		return irpfPercent;
	}

	public Mod145 setIrpfPercent(Double irpfPercent) {
		this.irpfPercent = irpfPercent;
		return this;
	}

	public boolean isDeductionHomeLoan() {
		return deductionHomeLoan;
	}

	public Mod145 setDeductionHomeLoan(boolean deductionHomeLoan) {
		this.deductionHomeLoan = deductionHomeLoan;
		return this;
	}

	public boolean isCeutaMelillaPalma() {
		return ceutaMelillaPalma;
	}

	public Mod145 setCeutaMelillaPalma(boolean ceutaMelillaPalma) {
		this.ceutaMelillaPalma = ceutaMelillaPalma;
		return this;
	}

	public List<IrpfDataAscendants> getAscendants() {
		return ascendants;
	}

	public Mod145 setAscendants(List<IrpfDataAscendants> ascendants) {
		this.ascendants = ascendants;
		return this;
	}

	public List<IrpfDataDescendients> getDescendients() {
		return descendients;
	}

	public Mod145 setDescendients(List<IrpfDataDescendients> descendients) {
		this.descendients = descendients;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod145 setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	
}
