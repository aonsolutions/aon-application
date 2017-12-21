package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod347 implements IFiscalModel, HasAudit {
	
	private static final long serialVersionUID = -7187516806216250344L;
	
	private Integer id;
	private int domain;	
	private int year;		
	private Administration administration;
	private String comments;
	private FiscalStatus status;  
	private boolean confidential;	
	private boolean complementary;
	private boolean replacement;
	private String number;
	private String replacedNumber;
	private String document;
	private String name;
	private String contactPhone;
	private String contactPerson;
	private String contactMail;
	private String representativeDocument;
	
	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private LinkedList<Mod347Declared> declared;
	private LinkedList<Mod347Asset> assets;
	
	@Override
	public Integer getId() { 
		return id;
	}

	public Mod347 setId(Integer id) {
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

	public Mod347 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod347 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}

	public Mod347 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}

	public Mod347 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	
	public Mod347 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}

	public Mod347 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public String getComments() {
		return comments;
	}

	public Mod347 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}

	public Mod347 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	public Mod347 setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public Mod347 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod347 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	@Override	
	public boolean isComplementary() {
		return complementary;
	}

	public Mod347 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Mod347 setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getReplacedNumber() {
		return replacedNumber;
	}

	public Mod347 setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
		return this;
	}
	
	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod347 setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}
	
	public LinkedList<Mod347Declared> getDeclared() {
		if (declared == null) {
			declared = new LinkedList<Mod347Declared>();
		}
		return declared;
	}
	
	public Mod347 setDeclared(LinkedList<Mod347Declared> declared) {
		this.declared = declared;
		return this;
	}
	
	public LinkedList<Mod347Asset> getAssets() {
		if (assets == null) {
			assets = new LinkedList<Mod347Asset>();
		}
		return assets;
	}
	
	public Mod347 setAssets(LinkedList<Mod347Asset> assets) {
		this.assets = assets;
		return this;
	}

	public FiscalStatus getStatus() {
		return status;
	}

	public Mod347 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	@Override
	public String getFullName() {
		return name;
	}
	
	@Override
	public double getResult() {
		return 0;
	}
	
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	
	@Override
	public String getSurname() {
		return null;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M347;
	}
	
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}
	
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod347 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod347 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod347 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod347 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}

	public String getContactMail() {
		return contactMail;
	}

	public Mod347 setContactMail(String contactMail) {
		this.contactMail = contactMail;
		return this;
	}
	
}
