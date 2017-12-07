package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;
import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod184 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = 7849527072278585773L;

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

	private int partnerTotal;
	private String entityType;
	private String mainActivity;
	private String foreignEntityType;
	private String foreignObject;
	private String country;
	private double residentPercent;
	private boolean taxIS;
	private double netSalesAmount;

	private String lrDocument;
	private String lrName;

	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	private LinkedList<Mod184Income> incomes;
	private LinkedList<Mod184Partner> partners;

	@Override
	public Integer getId() {
		return id;
	}
	public Mod184 setId(Integer id) {
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
	public Mod184 setDomain(int domain) {
		this.domain = domain;
		return this;
	}

	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod184 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	public int getEnterprise() {
		return enterprise;
	}
	public Mod184 setEnterprise(int enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M184;
	}
	
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}

	@Override
	public int getYear() {
		return year;
	}
	public Mod184 setYear(int year) {
		this.year = year;
		return this;
	}

	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod184 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	@Override
	public FiscalStatus getStatus() {
		return status;
	}
	public Mod184 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	public Mod184 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod184 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}

	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public Mod184 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}
	public Mod184 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public Mod184 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}
	public Mod184 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod184 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}
	public Mod184 setName(String name) {
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
	public Mod184 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}
	public Mod184 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public String getContactMail() {
		return contactMail;
	}
	public Mod184 setContactMail(String contactMail) {
		this.contactMail = contactMail;
		return this;
	}

	public int getPartnerTotal() {
		return partnerTotal;
	}
	public Mod184 setPartnerTotal(int partnerTotal) {
		this.partnerTotal = partnerTotal;
		return this;
	}

	public String getEntityType() {
		return entityType;
	}
	public Mod184 setEntityType(String entityType) {
		this.entityType = entityType;
		return this;
	}

	public String getMainActivity() {
		return mainActivity;
	}
	public Mod184 setMainActivity(String mainActivity) {
		this.mainActivity = mainActivity;
		return this;
	}

	public String getForeignEntityType() {
		return foreignEntityType;
	}
	public Mod184 setForeignEntityType(String foreignEntityType) {
		this.foreignEntityType = foreignEntityType;
		return this;
	}

	public String getForeignObject() {
		return foreignObject;
	}
	public Mod184 setForeignObject(String foreignObject) {
		this.foreignObject = foreignObject;
		return this;
	}

	public String getCountry() {
		return country;
	}
	public Mod184 setCountry(String country) {
		this.country = country;
		return this;
	}

	public double getResidentPercent() {
		return residentPercent;
	}
	public Mod184 setResidentPercent(double residentPercent) {
		this.residentPercent = residentPercent;
		return this;
	}

	public boolean isTaxIS() {
		return taxIS;
	}
	public Mod184 setTaxIS(boolean taxIS) {
		this.taxIS = taxIS;
		return this;
	}

	public double getNetSalesAmount() {
		return netSalesAmount;
	}
	public Mod184 setNetSalesAmount(double netSalesAmount) {
		this.netSalesAmount = netSalesAmount;
		return this;
	}

	public String getLrDocument() {
		return lrDocument;
	}
	public Mod184 setLrDocument(String lrDocument) {
		this.lrDocument = lrDocument;
		return this;
	}

	public String getLrName() {
		return lrName;
	}
	public Mod184 setLrName(String lrName) {
		this.lrName = lrName;
		return this;
	}

	public LinkedList<Mod184Income> getIncomes() {
		if (incomes == null) {
			incomes = new LinkedList<Mod184Income>();
		}
		return incomes;
	}
	public Mod184 setIncomes(LinkedList<Mod184Income> incomes) {
		this.incomes = incomes;
		return this;
	}

	public LinkedList<Mod184Partner> getPartners() {
		if (partners == null) {
			partners = new LinkedList<Mod184Partner>();
		}
		return partners;
	}
	public Mod184 setPartners(LinkedList<Mod184Partner> partners) {
		this.partners = partners;
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
	public Mod184 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod184 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod184 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod184 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
}
