package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod390 implements Serializable {

	private static final long serialVersionUID = 3703786573419236372L;
	
	private Integer id;
	private int domain;
	private int enterprise;
	private String enterpriseName;
	private int year;
	private byte administration;
	private boolean replacement;
	private String document;
	
	public Integer getId() {
		return id;
	}
	public Mod390 setId(Integer id) {
		this.id = id;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public Mod390 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public int getEnterprise() {
		return enterprise;
	}
	public Mod390 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public Mod390 setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}
	public int getYear() {
		return year;
	}
	public Mod390 setYear(int year) {
		this.year = year;
		return this;
	}
	public boolean is2013() {
		return (this.year == 2013); 
	}
	public byte getAdministration() {
		return administration;
	}
	public Mod390 setAdministration(byte administration) {
		this.administration = administration;
		return this;
	}
	public boolean isReplacement() {
		return replacement;
	}
	public Mod390 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	public String getDocument() {
		return document;
	}
	public Mod390 setDocument(String document) {
		this.document = document;
		return this;
	}

}

