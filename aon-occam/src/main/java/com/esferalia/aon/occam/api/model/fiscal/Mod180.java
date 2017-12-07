package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod180 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -1799677687517304432L;
	
	private Integer id;
	private int domain;
	private int enterprise;
	private int year;
	private Administration administration;
	private FiscalStatus status;
	private boolean confidential;
	private boolean replacement;
	private boolean complementary;
	private String receipt;
	private String replacedReceipt;
	private String comments;
	private String document;
	private String name;
	private String contactPerson;
	private String contactPhone;
	private String contactMail;
	private int receiverCountTotal;
	private double receiptTotal;
	private double retentionTotal;
	
	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private LinkedList<Mod180Detail> details;

	@Override
	public Integer getId() { 
		return id;
	}
	public Mod180 setId(Integer id) {
		this.id = id;
		return this;
	}
	public boolean isNew() {
		return id==null;
	}

	@Override
	public int getDomain() {
		return domain;
	}
	public Mod180 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod180 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}
	public Mod180 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M180;
	}
	
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}

	@Override
	public int getYear() {
		return year;
	}
	public Mod180 setYear(int year) {
		this.year = year;
		return this;
	}

	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod180 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	@Override
	public FiscalStatus getStatus() {
		return status;
	}
	public Mod180 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public Mod180 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod180 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public Mod180 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}
	public Mod180 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public Mod180 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}
	public Mod180 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod180 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	public Mod180 setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getSurname() {
		return null;
	}

	@Override
	public String getFullName() {
		return name;
	}

	public String getContactPerson() {
		return contactPerson;
	}
	public Mod180 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}
	public Mod180 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public String getContactMail() {
		return contactMail;
	}
	public Mod180 setContactMail(String contactMail) {
		this.contactMail = contactMail;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}
	public Mod180 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getReceiptTotal() {
		return receiptTotal;
	}
	public Mod180 setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}
	public Mod180 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public LinkedList<Mod180Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod180Detail>();
		}
		return details;
	}
	public Mod180 setDetails(LinkedList<Mod180Detail> details) {
		this.details = details;
		return this;
	}


	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	
	@Override
	public double getResult() {
		return 0;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod180 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod180 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod180 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod180 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
