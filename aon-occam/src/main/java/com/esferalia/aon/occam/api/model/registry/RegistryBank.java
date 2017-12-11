package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class RegistryBank implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private String bankAccount;
	private String bic;
	private String suffix;
	private String alias;
	private Integer account;
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
	public String getBankAccount() {
		return bankAccount;
	}
	public RegistryBank setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
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
	
}
