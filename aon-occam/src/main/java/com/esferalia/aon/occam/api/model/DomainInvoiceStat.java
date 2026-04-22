package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class DomainInvoiceStat implements Serializable {

	private static final long serialVersionUID = 7660434240269902891L;
	
	private Integer id;
	private Integer parentId;
	private String name;
	private String description;
	private String companyDocument;
	private String companyName;
	
	private int national;
	private int intracommunity;
	private int extracommunity;
	private int canCeuMel;
	private int otherISP;
	
	private int withholding;

	private int undeclaredIssued;
	private int undeclaredReceived;
	private int undeclaredSimplified;
	
	private int proformas;
	private int unrecordedIssued;
	private int unrecordedReceived;
	private int unrecordedSimplified;
	
	private int draft;
	private int inProcess;
	private int review;
	private int trash;
	
	public Integer getId() {
		return id;
	}
	public DomainInvoiceStat setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getParentId() {
		return parentId;
	}
	public DomainInvoiceStat setParentId(Integer parentId) {
		this.parentId = parentId;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public DomainInvoiceStat setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public DomainInvoiceStat setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getCompanyDocument() {
		return companyDocument;
	}
	public DomainInvoiceStat setCompanyDocument(String companyDocument) {
		this.companyDocument = companyDocument;
		return this;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public DomainInvoiceStat setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
	
	public int getNational() {
		return national;
	}
	public DomainInvoiceStat setNational(int national) {
		this.national = national;
		return this;
	}
	
	public int getIntracommunity() {
		return intracommunity;
	}
	public DomainInvoiceStat setIntracommunity(int intracommunity) {
		this.intracommunity = intracommunity;
		return this;
	}
	
	public int getExtracommunity() {
		return extracommunity;
	}
	public DomainInvoiceStat setExtracommunity(int extracommunity) {
		this.extracommunity = extracommunity;
		return this;
	}
	
	public int getCanCeuMel() {
		return canCeuMel;
	}
	public DomainInvoiceStat setCanCeuMel(int canCeuMel) {
		this.canCeuMel = canCeuMel;
		return this;
	}
	
	public int getOtherISP() {
		return otherISP;
	}
	public DomainInvoiceStat setOtherISP(int otherISP) {
		this.otherISP= otherISP;
		return this;
	}
	
	public int getWithholding() {
		return withholding;
	}
	public DomainInvoiceStat setWithholding(int withholding) {
		this.withholding = withholding;
		return this;
	}
	
	public int getUndeclaredIssued() {
		return undeclaredIssued;
	}
	public DomainInvoiceStat setUndeclaredIssued(int undeclaredIssued) {
		this.undeclaredIssued = undeclaredIssued;
		return this;
	}
	
	public int getUndeclaredReceived() {
		return undeclaredReceived;
	}
	public DomainInvoiceStat setUndeclaredReceived(int undeclaredReceived) {
		this.undeclaredReceived = undeclaredReceived;
		return this;
	}
	
	public int getUndeclaredSimplified() {
		return undeclaredSimplified;
	}
	public DomainInvoiceStat setUndeclaredSimplified(int undeclaredSimplified) {
		this.undeclaredSimplified = undeclaredSimplified;
		return this;
	}
	
	public int getProformas() {
		return proformas;
	}
	public DomainInvoiceStat setProformas(int proformas) {
		this.proformas = proformas;
		return this;
	}
	
	public int getUnrecordedIssued() {
		return unrecordedIssued;
	}
	public DomainInvoiceStat setUnrecordedIssued(int unrecordedIssued) {
		this.unrecordedIssued = unrecordedIssued;
		return this;
	}
	
	public int getUnrecordedReceived() {
		return unrecordedReceived;
	}
	public DomainInvoiceStat setUnrecordedReceived(int unrecordedReceived) {
		this.unrecordedReceived = unrecordedReceived;
		return this;
	}
	
	public int getUnrecordedSimplified() {
		return unrecordedSimplified;
	}
	public DomainInvoiceStat setUnrecordedSimplified(int unrecordedSimplified) {
		this.unrecordedSimplified = unrecordedSimplified;
		return this;
	}
	
	public int getDraft() {
		return draft;
	}
	public DomainInvoiceStat setDraft(int draft) {
		this.draft = draft;
		return this;
	}
	
	public int getInProcess() {
		return inProcess;
	}
	public DomainInvoiceStat setInProcess(int inProcess) {
		this.inProcess = inProcess;
		return this;
	}
	
	public int getReview() {
		return review;
	}
	public DomainInvoiceStat setReview(int review) {
		this.review = review;
		return this;
	}
	
	public int getTrash() {
		return trash;
	}
	public DomainInvoiceStat setTrash(int trash) {
		this.trash = trash;
		return this;
	}
	
	public void addNational(int c) {this.national += c;}
	public void addIntracommunity(int c) {this.intracommunity += c;}
	public void addExtracommunity(int c) {this.extracommunity += c;}
	public void addCanCeuMel(int c) {this.canCeuMel += c;}
	public void addOtherISP(int c) {this.otherISP += c;}
	public void addWithholding(int c) {this.withholding += c;}
	
	public void addUndeclaredIssued(int c) {this.undeclaredIssued += c;}
	public void addUndeclaredReceived(int c) {this.undeclaredReceived += c;}
	public void addUndeclaredSimplified(int c) {this.undeclaredSimplified += c;}
	public void addProformas(int c) {this.proformas += c;}
	
	public void addUnrecordedIssued(int c) {this.unrecordedIssued += c;}
	public void addUnrecordedReceived(int c) {this.unrecordedReceived += c;}
	public void addUnrecordedSimplified(int c) {this.unrecordedSimplified += c;}
	
	public void addDraft(int c) {this.draft += c;}
	public void addInProcess(int c) {this.inProcess += c;}
	public void addReview(int c) {this.review += c;}
	public void addTrash(int c) {this.trash += c;}
	
	
}
