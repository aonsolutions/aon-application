package com.esferalia.aon.occam.api.model.fiscal;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FiscalMenuItem implements IFiscalModel {
	
	private static final long serialVersionUID = -5070713371126172182L;
	
	private Integer id;
	private int domain;
	private String domainName;
	private FiscalModelType model;
	private int year;
	private Period period;
	private Administration administration;
	private FiscalStatus status;
	private IFiscalModelKey declarationTypeKey;
	private FiscalModelDeclarationType declarationType;
	private boolean complementary;
	private boolean replacement;
	private String document;
	private String name;
	private String surname;
	private double result;
	private Finance finance;
	
	@Override
	public Integer getId() {
		return id;
	}
	public FiscalMenuItem setId(Integer id) {
		this.id = id;
		return this;
	}
	
	@Override
	public int getDomain() {
		return domain;
	}
	public FiscalMenuItem setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	
	@Override
	public String getDomainName() {
		return domainName;
	}
	public FiscalMenuItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public FiscalModelType getModel() {
		return model;
	}
	public FiscalMenuItem setModel(FiscalModelType model) {
		this.model = model;
		return this;
	}
	
	@Override
	public int getYear() {
		return year;
	}
	public FiscalMenuItem setYear(int year) {
		this.year = year;
		return this;
	}
	
	@Override
	public Period getPeriod() {
		return period;
	}
	public FiscalMenuItem setPeriod(Period period) {
		this.period = period;
		return this;
	}
	
	@Override
	public Administration getAdministration() {
		return administration;
	}
	public FiscalMenuItem setAdministration(Administration administration) {
		this.administration = administration;
		return this;
	}
	
	@Override
	public FiscalStatus getStatus() {
		return status;
	}
	public FiscalMenuItem setStatus(FiscalStatus status) {
		this.status = status;
		return this;
	}
	
	@Override
	public IFiscalModelKey getDeclarationTypeKey() {
		return declarationTypeKey;
	}
	public FiscalMenuItem setDeclarationTypeKey(IFiscalModelKey declarationTypeKey) {
		this.declarationTypeKey = declarationTypeKey;
		return this;
	}
	
	@Override
	public FiscalModelDeclarationType getDeclarationType() {
		return declarationType;
	}
	public FiscalMenuItem setDeclarationType(FiscalModelDeclarationType declarationType) {
		this.declarationType = declarationType;
		return this;
	}
	
	@Override
	public boolean isComplementary() {
		return complementary;
	}
	public FiscalMenuItem setComplementary(boolean complementary) {
		this.complementary = complementary;
		return this;
	}
	
	@Override
	public boolean isReplacement() {
		return replacement;
	}
	public FiscalMenuItem setReplacement(boolean replacement) {
		this.replacement = replacement;
		return this;
	}
	
	@Override
	public String getDocument() {
		return document;
	}
	public FiscalMenuItem setDocument(String document) {
		this.document = document;
		return this;
	}
	
	@Override
	public String getName() {
		return name;
	}
	public FiscalMenuItem setName(String name) {
		this.name = name;
		return this;
	}
	
	@Override
	public String getSurname() {
		return surname;
	}
	public FiscalMenuItem setSurname(String surname) {
		this.surname = surname;
		return this;
	}
	
	@Override
	public double getResult() {
		return result;
	}
	public FiscalMenuItem setResult(double result) {
		this.result = result;
		return this;
	}
	
	
	@Override
	public Finance getFinance() {
		return finance;
	}
	public FiscalMenuItem setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}

	@Override
	public String getFullName() {
		return (isEntity())
			?name
			:AonStringUtils.prependIfMissing(getName(),AonStringUtils.appendIfMissing(
					AonStringUtils.trimToNull(getSurname()), ", " ) ); 
	}

	public boolean isEntity() {
		return AonDocumentUtil.isEntity(getDocument());
	}
	
	
}
