package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.watson.util.AonStringUtils;

public class CertificateInfo implements Serializable {
	
	// ----------------------------- Variables

	private static final long serialVersionUID = 1L;
	
	private String enterprise;
	private String ocupation;
	private String cif;
	private String type;
	private String surname;
	private String name;
	private String document;
	private Date fromDate;
	private Date toDate;
	
	// ----------------------------- Constructor
	
	public CertificateInfo() {
		super();
	}
	
	// ----------------------------- Getter / Setter

	public String getEnterprise() {
		return enterprise;
	}

	public CertificateInfo setEnterprise(String enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public String getOcupation() {
		return ocupation;
	}

	public CertificateInfo setOcupation(String ocupation) {
		this.ocupation = ocupation;
		return this;
	}

	public String getCif() {
		return cif;
	}

	public CertificateInfo setCif(String cif) {
		this.cif = cif;
		return this;
	}

	public String getType() {
		return type;
	}

	public CertificateInfo setType(String type) {
		this.type = type;
		return this;
	}

	public String getSurname() {
		return surname;
	}

	public CertificateInfo setSurname(String surname) {
		this.surname = surname;
		return this;
	}

	public String getName() {
		return name;
	}

	public CertificateInfo setName(String name) {
		this.name = name;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public CertificateInfo setDocument(String document) {
		this.document = document;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public CertificateInfo setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}

	public CertificateInfo setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}
	
	// ----------------------------- ToString
	
	@Override
	public String toString() {
		String result = "";
		
		if(AonStringUtils.isNotBlank(enterprise)) result += "Empresa : " + enterprise;
		if(AonStringUtils.isNotBlank(ocupation)) result += "<br>Ocupaci\u00F3n : " + ocupation;
		if(AonStringUtils.isNotBlank(cif)) result += "<br>CIF : " + cif;
		if(AonStringUtils.isNotBlank(type)) result += "<br>Tipo : " + type;
		
		if(AonStringUtils.isBlank(enterprise)) result += "Nombre : " + surname + ", " + name;
		else result += "<br>Nombre : " + surname + ", " + name;
		
		result += "<br>Documento : " + document;
		
		return result;
	}
	
	public boolean isEmpty() {
		return getEnterprise() == null && getOcupation() == null && getCif() == null
			&& getType() == null && getSurname() == null && getName() == null 
			&& getDocument() == null && getFromDate() == null && getToDate() == null;
	}
	
}
