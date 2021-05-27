package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390 implements IFiscalModel, HasAudit {
	private static final long serialVersionUID = 3703786573419236372L;
	
	private static final String LEGAL_ENTITY_PATTERN = "^[0-9|X|Y|Z].*";
	
	private Integer id;
	private int domain;
	private String domainName;
	private int enterprise;
	private String enterpriseName;
	private int year;
	private Administration administration;
	private FiscalStatus status;
	private boolean replacement;
	private boolean complementary;
	private boolean withoutActivity;
	private String document;
	private String name;
	private String firstSurname;
	private String secondSurname;
	private String contactPhone;
	private String receipt;
	private String replacedReceipt;
	private String comments;
	private String xmlFormat;
	
	private boolean oldStyle;
	
	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;

	@Override
	public Integer getId() {
		return id;
	}
	public Mod390 setId(Integer id) {
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
	public Mod390 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return this.domainName;
	}
	public Mod390 setDomainName(String domainName) {
		this.domainName = domainName;
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
	
	@Override
	public int getYear() {
		return year;
	}
	public Mod390 setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M390;
	}
	
	@Override
	public Period getPeriod() {
		return Period.YEAR;
	}

	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod390 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	@Override
	public FiscalStatus getStatus() {
		return this.status;
	}
	public Mod390 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod390 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public Mod390 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	public boolean isWithoutActivity() {
		return withoutActivity;
	}
	public Mod390 setWithoutActivity(boolean withoutActivity) {
		this.withoutActivity = withoutActivity;
		return this;
	}

	
	public boolean isOldStyle() {
		return oldStyle;
	}
	public Mod390 setOldStyle(boolean oldStyle) {
		this.oldStyle = oldStyle;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod390 setDocument(String document) {
		this.document = document;
		return this;
	}
	
	public boolean isLegalEntity() {
		return AonStringUtils.isNotEmpty(getDocument()) && !getDocument().matches(LEGAL_ENTITY_PATTERN);
	}

	@Override
	public String getName() {
		return name;
	}
	public Mod390 setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getFullName() {
		return name;
	}

	public String getFirstSurname() {
		return firstSurname;
	}
	public Mod390 setFirstSurname(String firstSurname) {
		this.firstSurname = firstSurname;
		return this;
	}
	public String getSecondSurname() {
		return secondSurname;
	}
	public Mod390 setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
		return this;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public Mod390 setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
		return this;
	}

	public String getReceipt() {
		return receipt;
	}
	public Mod390 setReceipt(String receipt) {
		this.receipt = receipt;
		return this;
	}

	public String getReplacedReceipt() {
		return replacedReceipt;
	}
	public Mod390 setReplacedReceipt(String replacedReceipt) {
		this.replacedReceipt = replacedReceipt;
		return this;
	}

	public String getComments() {
		return comments;
	}
	public Mod390 setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public String getXmlFormat() {
		return this.xmlFormat;
	}
	public Mod390 setXmlFormat(String xmlFormat) {
		this.xmlFormat = xmlFormat;
		return this;
	}
	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod390 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod390 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod390 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod390 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
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
	@Override
	public String getSurname() {
		return null;
	}
	public boolean isReplacementDeclarationAvailable() {
		return true;
	}
	public boolean isComplementaryDeclarationAvailable() {
		return false;
	}
}

