package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;

public class Mod369Detail implements Serializable {
	
	private static final long serialVersionUID = 1898784811277135028L;

	private Integer id;	
	private int domain;
	private int mod369;
	
	private Country country;       // Código de país/EM de consumo
	private double vatPercent;     // Tipo (%) de IVA
	private Mod369VatType vatType; // Tipo IVA
	private double base;           // Base imponible
	private double quota;          // Cuota IVA
	
	private boolean dirty;
	private boolean deleted;
	private int tempId;

	public Integer getId() {
		return id;
	}

	public Mod369Detail setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod369Detail setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod369() {
		return mod369;
	}

	public Mod369Detail setMod369(int mod369) {
		this.mod369 = mod369;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod369Detail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod369Detail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public Mod369Detail setCountry(Country country) {
		this.country = country;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod369Detail setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	public double getVatPercent() {
		return vatPercent;
	}

	public Mod369Detail setVatPercent(double vatPercent) {
		this.vatPercent = vatPercent;
		return this;
	}

	public Mod369VatType getVatType() {
		return vatType;
	}

	public Mod369Detail setVatType(Mod369VatType vatType) {
		this.vatType = vatType;
		return this;
	}

	public double getBase() {
		return base;
	}

	public Mod369Detail setBase(double base) {
		this.base = base;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public Mod369Detail setQuota(double quota) {
		this.quota = quota;
		return this;
	}

}
