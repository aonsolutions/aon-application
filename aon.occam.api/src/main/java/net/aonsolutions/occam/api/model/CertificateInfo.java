package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class CertificateInfo implements Serializable {
	
	private static final long serialVersionUID = 2818375916299833442L;
	
	private String enterprise;
	private String ocupation;
	private String cif;
	private String type;
	private String surname;
	private String name;
	private String document;
	private Date fromDate;
	private Date toDate;
	
	public CertificateInfo() {
		super();
	}
	
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
	
}
