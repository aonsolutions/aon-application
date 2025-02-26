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
	
	private Country originalCountry;       // Código de país/EM de consumo (valor original, cuando se crea la línea de detalle de forma automática)
	private double originalVatPercent;     // Tipo (%) de IVA (valor original, cuando se crea la línea de detalle de forma automática)
	private double originalBase;           // Base imponible (valor original, cuando se crea la línea de detalle de forma automática)
	private double originalQuota;          // Cuota IVA (valor original, cuando se crea la línea de detalle de forma automática)

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

	public Country getOriginalCountry() {
		return originalCountry;
	}

	public Mod369Detail setOriginalCountry(Country originalCountry) {
		this.originalCountry = originalCountry;
		return this;
	}

	public double getOriginalVatPercent() {
		return originalVatPercent;
	}

	public Mod369Detail setOriginalVatPercent(double originalVatPercent) {
		this.originalVatPercent = originalVatPercent;
		return this;
	}

	public double getOriginalBase() {
		return originalBase;
	}

	public Mod369Detail setOriginalBase(double originalBase) {
		this.originalBase = originalBase;
		return this;
	}

	public double getOriginalQuota() {
		return originalQuota;
	}

	public Mod369Detail setOriginalQuota(double originalQuota) {
		this.originalQuota = originalQuota;
		return this;
	}
	
	// Línea de detalle introducida o modificada manualmente
	public boolean isManual() {
		return (this.originalCountry == null && this.originalVatPercent == 0.0 && this.originalBase == 0.0 && this.originalQuota == 0.0) ||   // Línea introducida manualmente (los datos originales están vacios)          
		       (this.country != this.originalCountry || this.vatPercent != this.originalVatPercent || this.base != this.originalBase || this.quota != this.originalQuota);   // Línea modificada manualmente (alguno de los datos originales son distintos de los actuales)
	}

}
