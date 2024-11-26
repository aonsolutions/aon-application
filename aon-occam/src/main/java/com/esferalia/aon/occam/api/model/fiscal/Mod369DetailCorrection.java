package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod369DetailCorrection implements Serializable {
	
	private static final long serialVersionUID = 8605621921751822496L;

	private Integer id;	
	private int domain;
	private int mod369;
	
	private Country country;       // Código de país/EM de consumo
	private int year;              // Ejercicio
	private Period period;         // Periodo
	private double quota;          // Cuota IVA corregida
	
	private boolean dirty;
	private boolean deleted;
	private int tempId;

	public Integer getId() {
		return id;
	}

	public Mod369DetailCorrection setId(Integer id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod369DetailCorrection setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public int getMod369() {
		return mod369;
	}

	public Mod369DetailCorrection setMod369(int mod369) {
		this.mod369 = mod369;
		return this;
	}

	public boolean isDirty() {
		return dirty;
	}

	public Mod369DetailCorrection setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod369DetailCorrection setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public Mod369DetailCorrection setCountry(Country country) {
		this.country = country;
		return this;
	}

	public int getTempId() {
		return tempId;
	}

	public Mod369DetailCorrection setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public Mod369DetailCorrection setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public int getYear() {
		return year;
	}

	public Mod369DetailCorrection setYear(int year) {
		this.year = year;
		return this;
	}

	public Period getPeriod() {
		return period;
	}

	public Mod369DetailCorrection setPeriod(Period period) {
		this.period = period;
		return this;
	}

}
