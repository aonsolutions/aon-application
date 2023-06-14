package net.aonsolutions.infovox.model;

import java.util.Optional;

public class OCRCompany {
	private String id;
	private String account;
	private String environment;
	private String name;
	private String countryCode;
	private String taxId;
	private Integer accountingPeriodLength;
	private String creator;
	private String creation;
//	             "data": {
//	               "custom": {},
//	               "accounting": {},
//	               "aeat": {},
//	               "odoo": {},
//	               "workflow": {}
//	             }
	public String getId() {
		return id;
	}
	public OCRCompany setId(String id) {
		this.id = id;
		return this;
	}
	
	public Optional<String> getAccount() {
		return Optional.ofNullable(account);
	}
	public OCRCompany setAccount(String account) {
		this.account = account;
		return this;
	}
	
	public Optional<String> getEnvironment() {
		return Optional.ofNullable(environment);
	}
	public OCRCompany setEnvironment(String environment) {
		this.environment = environment;
		return this;
	}
	
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}
	public OCRCompany setName(String name) {
		this.name = name;
		return this;
	}
	
	public Optional<String> getCountryCode() {
		return Optional.ofNullable(countryCode);
	}
	public OCRCompany setCountryCode(String countryCode) {
		this.countryCode = countryCode;
		return this;
	}
	
	public Optional<String> getTaxId() {
		return Optional.ofNullable(taxId);
	}
	public OCRCompany setTaxId(String taxId) {
		this.taxId = taxId;
		return this;
	}
	
	public Optional<Integer> getAccountingPeriodLength() {
		return Optional.ofNullable(accountingPeriodLength);
	}
	public OCRCompany setAccountingPeriodLength(Integer accountingPeriodLength) {
		this.accountingPeriodLength = accountingPeriodLength;
		return this;
	}
	
	public Optional<String> getCreator() {
		return Optional.ofNullable(creator);
	}
	public OCRCompany setCreator(String creator) {
		this.creator = creator;
		return this;
	}
	
	public Optional<String> getCreation() {
		return Optional.ofNullable(creation);
	}
	public OCRCompany setCreation(String creation) {
		this.creation = creation;
		return this;
	}
	
	
}
