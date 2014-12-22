package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Mod190Detail implements Serializable {

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

	private IrpfData irpfData;
	private IrpfResult irpfResult;

	private boolean dirty;
	private boolean deleted;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDomain() {
		return domain;
	}

	public void setDomain(int domain) {
		this.domain = domain;
	}

	public int getMod190() {
		return mod190;
	}

	public void setMod190(int mod190) {
		this.mod190 = mod190;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
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

	public IrpfData getIrpfData() {
		return irpfData;
	}

	public void setIrpfData(IrpfData irpfData) {
		this.irpfData = irpfData;
	}

	public IrpfResult getIrpfResult() {
		return irpfResult;
	}

	public void setIrpfResult(IrpfResult irpfResult) {
		this.irpfResult = irpfResult;
	}
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
