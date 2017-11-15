package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Mod349Key;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod349Detail implements Serializable {
	
	private static final long serialVersionUID = 3884116555852706575L;
	
	private Integer id;	
	private int domain;
	
	private Mod349 mod349;
	
	private Mod349Key type;  // Clave de operación
	private Integer registry;
	private Country country;
	private String document;
	private String name;	
	private double accumulated;
	private double declared;
	private double amount;	
	private boolean rectification;
	private Integer rectifiedYear;
	private Period rectifiedPeriod;
	private double rectifiedAmount;	

	private boolean dirty;
	private boolean deleted;
	private int tempId;

	public Integer getId() {
		return id;
	}

	public Mod349Detail setId(Integer id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Mod349Detail setName(String name) {
		this.name = name;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public Mod349Detail setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public Mod349 getMod349() {
		return mod349;
	}

	public Mod349Detail setMod349(Mod349 mod349) {
		this.mod349 = mod349;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public Mod349Detail setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}

	public Mod349Detail setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public Mod349Detail setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public boolean isRectification() {
		return rectification;
	}

	public Mod349Detail setRectification(boolean rectification) {
		this.rectification = rectification;
		return this;
	}

	public Mod349Key getType() {
		return type;
	}

	public Mod349Detail setType(Mod349Key type) {
		this.type = type;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Mod349Detail setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}

	public Country getCountry() {
		return country;
	}

	public Mod349Detail setCountry(Country country) {
		this.country = country;
		return this;
	}

	public double getAccumulated() {
		return accumulated;
	}

	public Mod349Detail setAccumulated(double accumulated) {
		this.accumulated = accumulated;
		return this;
	}

	public double getAmount() {
		return amount;
	}

	public Mod349Detail setAmount(double amount) {
		this.amount = amount;
		return this;
	}

	public double getDeclared() {
		return declared;
	}

	public Mod349Detail setDeclared(double declared) {
		this.declared = declared;
		return this;
	}

	public Integer getRectifiedYear() {
		return rectifiedYear;
	}

	public Mod349Detail setRectifiedYear(Integer rectifiedYear) {
		this.rectifiedYear = rectifiedYear;
		return this;
	}

	public Period getRectifiedPeriod() {
		return rectifiedPeriod;
	}

	public Mod349Detail setRectifiedPeriod(Period rectifiedPeriod) {
		this.rectifiedPeriod = rectifiedPeriod;
		return this;
	}

	public double getRectifiedAmount() {
		return rectifiedAmount;
	}

	public Mod349Detail setRectifiedAmount(double rectifiedAmount) {
		this.rectifiedAmount = rectifiedAmount;
		return this;
	}
	
	public int getTempId() {
		return tempId;
	}

	public Mod349Detail setTempId(int tempId) {
		this.tempId = tempId;
		return this;
	}

	
}
