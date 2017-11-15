package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;

public class Mod349 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = 6039224124853621183L;
	
	private Integer id;
	private int domain;	
	private int year;
	private Period period;	
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
	private boolean periodicityChange;
	private String representativeDocument;
	
	private String domainName;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	private LinkedList<Mod349Detail> details;
	
	@Override
	public Integer getId() { 
		return id;
	}

	public Mod349 setId(Integer id) {
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

	public Mod349 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod349 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public int getYear() {
		return year;
	}

	public Mod349 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Period getPeriod() {
		return period;
	}

	public Mod349 setPeriod(Period period) {
		this.period = period;
		return this;
	}

	@Override
	public Administration getAdministration() {
		return administration;
	}

	public Mod349 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}

	public boolean isConfidential() {
		return confidential;
	}
	
	public Mod349 setConfidential(boolean confidential) {
		this.confidential = confidential;
		return this;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}

	public Mod349 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public String getComments() {
		return comments;
	}

	public Mod349 setComments(String comments) {
		this.comments = comments;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}

	public Mod349 setDocument(String document) {
		this.document = document;
		return this;
	}

	@Override
	public String getName() {
		return name;
	}

	public Mod349 setName(String name) {
		this.name = name;
		return this;
	}
	
	public String getContactPerson() {
		return contactPerson;
	}

	public Mod349 setContactPerson(String contactPerson) {
		this.contactPerson = contactPerson;
		return this;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public Mod349 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	@Override	
	public boolean isComplementary() {
		return complementary;
	}

	public Mod349 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Mod349 setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getReplacedNumber() {
		return replacedNumber;
	}

	public Mod349 setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
		return this;
	}
	
	public boolean isPeriodicityChange() {
		return periodicityChange;
	}

	public Mod349 setPeriodicityChange(boolean periodicityChange) {
		this.periodicityChange = periodicityChange;
		return this;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public Mod349 setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
		return this;
	}
	
	public LinkedList<Mod349Detail> getDetails() {
		if (details == null) {
			details = new LinkedList<Mod349Detail>();
		}
		return details;
	}

	public FiscalStatus getStatus() {
		return status;
	}

	public Mod349 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	public Mod349 setDetails(LinkedList<Mod349Detail> details) {
		this.details = details;
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
		return FiscalModelType.M349;
	}
	
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod349 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod349 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod349 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod349 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	
}
