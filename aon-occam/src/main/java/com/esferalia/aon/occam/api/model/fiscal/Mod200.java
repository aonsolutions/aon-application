package com.esferalia.aon.occam.api.model.fiscal;

import java.util.Date;

import com.esferalia.aon.occam.api.model.HasAudit;
import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod200 implements IFiscalModel, HasAudit {

	private static final long serialVersionUID = -7631557093162872234L;
	
	private Integer id;
	private int domain;
	private String domainName;
	private int year;
	private Finance finance;
	private Administration administration;
	private FiscalStatus status;
	private boolean complementary;
	private boolean replacement;
	private String number;
	private String replacedNumber;
	private String comments;
	private String document;
	private String surname;
	private String name;
	private String resultType;
	private double result;

	private String creationUser;
	private Date creationDate;
	private String modificationUser;
	private Date modificationDate;
	
	@Override
	public Integer getId() {
		return id;
	}
	public Mod200 setId(Integer id) {
		this.id = id;
		return this;
	}
	
	@Override
	public int getDomain() {
		return domain;
	}
	public Mod200 setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public Mod200 setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	@Override
	public FiscalModelType getModel() {
		return FiscalModelType.M200;
	}
	
	@Override
	public int getYear() {
		return year;
	}
	public Mod200 setYear(int year) {
		this.year = year;
		return this;
	}
	
	public Finance getFinance() {
		return finance;
	}
	public Mod200 setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public Mod200 setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	public Mod200 setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	@Override
	public FiscalStatus getStatus() {
		return this.status;
	}
	public boolean isFinished() {
		return getStatus() == FiscalStatus.FINISHED;
	}
	public boolean isNotFinished() {
		return getStatus() != FiscalStatus.FINISHED;
	}

	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public Mod200 setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	public boolean isComplementary() {
		return complementary;
	}
	public Mod200 setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}

	@Override
	public String getDocument() {
		return document;
	}
	public Mod200 setDocument(String document) {
		this.document = document;
		return this;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public Mod200 setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public String getSurname() {
		return surname;
	}
	public Mod200 setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	@Override
	public String getFullName() {
		return getName(); 
	}
	
	public String getNumber() {
		return number;
	}
	public Mod200 setNumber(String number) {
		this.number = number;
		return this;
	}
	
	public String getReplacedNumber() {
		return replacedNumber;
	}
	public Mod200 setReplacedNumber(String replacedNumber) {
		this.replacedNumber = replacedNumber;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	public Mod200 setComments(String comments) {
		this.comments = comments;
		return this;
	}
	
	public String getResultType() {
		return resultType;
	}
	public boolean isPayback() {
		return AonStringUtils.equalsIgnoreCase(resultType, "D");
	}
	public boolean isDeposit() {
		return AonStringUtils.equalsIgnoreCase(resultType, "I");
	}
	public Mod200 setResultType(String resultType) {
		this.resultType = resultType;
		return this;
	}
	
	@Override
	public double getResult() {
		return result;
	}
	public Mod200 setResult(double result) {
		this.result = result;
		return this;
	}

	// ---------------------------------------------------------- AUDIT
	@Override
	public String getCreationUser() {
		return creationUser;
	}
	public Mod200 setCreationUser(String creationUser) {
		this.creationUser = creationUser;
		return this;
	}
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	public Mod200 setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
		return this;
	}
	@Override
	public String getModificationUser() {
		return modificationUser;
	}
	public Mod200 setModificationUser(String modificationUser) {
		this.modificationUser = modificationUser;
		return this;
	}
	@Override
	public Date getModificationDate() {
		return modificationDate;
	}
	public Mod200 setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
		return this;
	}
	

	// ------------------------------------------------------------- TODO
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return null;
	}
	@Override
	public Period getPeriod() {
		return null;
	}

}
