package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.watson.util.AonStringUtils;

public class RegistryBank implements Serializable {
	
	private static final long serialVersionUID = 3208247929297185256L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private BankAccount bankAccount;
	private String bic;
	private String suffix;
	private String alias;
	private Integer account;
	private String accountCode;
	private String accountDescription;
	private Boolean active;

	
	public RegistryBank() {
	
	}
	
	public Integer getId() {
		return id;
	}
	public RegistryBank setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public RegistryBank setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getRegistry() {
		return registry;
	}
	public RegistryBank setRegistry(Integer registry) {
		this.registry = registry;
		return this;
	}
	public BankAccount getBankAccount() {
		return bankAccount;
	}
	public RegistryBank setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	public String getAccountCode() {
		return accountCode;
	}
	public RegistryBank setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}
	public RegistryBank setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;

	}

	public String getBic() {
		return bic;
	}
	public RegistryBank setBic(String bic) {
		this.bic = bic;
		return this;
	}
	public String getSuffix() {
		return suffix;
	}
	public RegistryBank setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public RegistryBank setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	public Integer getAccount() {
		return account;
	}
	public RegistryBank setAccount(Integer account) {
		this.account = account;
		return this;
	}
	public Boolean isActive() {
		return active;
	}
	public RegistryBank setActive(Boolean active) {
		this.active = active;
		return this;
	}
	
	public String getFullName() {
		StringBuffer sb = new StringBuffer();
		if (getBankAccount() != null && !AonStringUtils.isBlank(getBankAccount().getBban())) {
			sb.append(getFullName(getBankAccount().toString()));
		}
		return sb.toString(); 
	}
	
	private String getFullName(String bankAccount) {
		StringBuffer sb = new StringBuffer();
		sb.append(bankAccount);
		sb.append(" ");
		if (!AonStringUtils.isBlank(getBic())) {
			sb.append("[");
			sb.append(getBic());
			sb.append("] ");
		}
		if (!AonStringUtils.isBlank(getAlias())) {
			sb.append(getAlias());	
		}
		return sb.toString(); 
	}
	
}
