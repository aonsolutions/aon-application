package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod190 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -853765073923154482L;
	
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
	private int receiverCountTotal;
	private double receiptTotal;
	private double retentionTotal;
	
	private LinkedList<Mod190Detail> details;
	
	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	

	@Override
	public Integer getId() { 
		return id;
	}
	public Mod190 setId(Integer id) {
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
	public Mod190 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod190 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}

	public Mod190 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}
	public Mod190 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod190 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	@Override
	public FiscalStatus getStatus() {
		return status;
	}
	public Mod190 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public Mod190 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod190 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public Mod190 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}
	public Mod190 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public Mod190 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}
	public Mod190 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod190 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	public Mod190 setName(String name) {
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
	public Mod190 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}
	public Mod190 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public int getReceiverCountTotal() {
		return receiverCountTotal;
	}
	public Mod190 setReceiverCountTotal(int receiverCountTotal) {
		this.receiverCountTotal = receiverCountTotal;
		return this;
	}

	public double getReceiptTotal() {
		return receiptTotal;
	}
	public Mod190 setReceiptTotal(double receiptTotal) {
		this.receiptTotal = receiptTotal;
		return this;
	}

	public double getRetentionTotal() {
		return retentionTotal;
	}
	public Mod190 setRetentionTotal(double retentionTotal) {
		this.retentionTotal = retentionTotal;
		return this;
	}

	public LinkedList<Mod190Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod190Detail>();
		}
		return details;
	}
	public Mod190 setDetails(LinkedList<Mod190Detail> details) {
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
	public Mod190 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod190 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod190 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod190 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}


	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M190;
	}

	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}

}
