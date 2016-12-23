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
	private IrpfData irpfData;
	private IrpfResult irpfResult;

	private double perceptionIL;
	private double retentionIL;
	private double outputRetentionIL;
	
	private boolean dirty;
	private boolean deleted;

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

	public IrpfData getIrpfData() {
		return irpfData;
	}

	public Mod190Detail setIrpfData(IrpfData irpfData) {
		this.irpfData = irpfData;
		return this;
	}

	public IrpfResult getIrpfResult() {
		return irpfResult;
	}

	public Mod190Detail setIrpfResult(IrpfResult irpfResult) {
		this.irpfResult = irpfResult;
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

	
}
