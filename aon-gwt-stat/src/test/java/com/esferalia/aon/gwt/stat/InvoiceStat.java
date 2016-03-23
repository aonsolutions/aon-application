package com.esferalia.aon.gwt.stat;

import java.io.Serializable;
import java.util.Date;

public class InvoiceStat implements Serializable {

	private static final long serialVersionUID = -5891085619906291376L;

	// StatTest
	private Date issueDate;
	private Double taxableBase;

	// StatTest3 tabla invoice
	private String rname;
	private int registry;

	public Date getIssueDate() {
		return issueDate;
	}

	public InvoiceStat setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public Double getTaxableBase() {
		return taxableBase;
	}

	public InvoiceStat setTaxableBase(Double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}

	public String getRname() {
		return rname;
	}

	public InvoiceStat setRname(String rname) {
		this.rname = rname;
		return this;
	}

	public int getRegistry() {
		return registry;
	}

	public InvoiceStat setRegistry(int registry) {
		this.registry = registry;
		return this;
	}

	// getInvoicesGrafico1 + IssueDate que ya está declarada
	private byte type;
	private int count;
	private int issueDateMonth;

	public byte getType() {
		return type;
	}

	public InvoiceStat setType(byte type) {
		this.type = (byte) type;
		return this;
	}
	
	public int getCount() {
		return count;
	}

	public InvoiceStat setCount(int count) {
		this.count = count;
		return this;
	}

	public int getIssueDateMonth() {
		return issueDateMonth;
	}

	public InvoiceStat setIssueDateMonth(int issueDateMonth) {
		this.issueDateMonth = issueDateMonth;
		return this;
	}

	// StatTest3 tabla Registry
	private int id;
	private int domain;
	private String nationality;

	public int getId() {
		return id;
	}

	public InvoiceStat setId(int id) {
		this.id = id;
		return this;
	}

	public int getDomain() {
		return domain;
	}

	public InvoiceStat setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	public String getNationality() {
		return nationality;
	}

	public InvoiceStat setNationality(String nationality) {
		this.nationality = nationality;
		return this;
	}

}
