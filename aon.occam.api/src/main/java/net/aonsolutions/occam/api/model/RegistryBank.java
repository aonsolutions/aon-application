package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public class RegistryBank implements Serializable {
	
	private static final long serialVersionUID = 3208247929297185256L;
	
	private boolean deleted;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private BankAccount bankAccount;
	private String bic;
	private String suffix;
	private String alias;
	private boolean active;
	private Account account;
	private String requisition;
	private String sepaMandateRef;
	private double balance;
	private double availableBalance;
	private Date balanceDate;
	
	public boolean isDeleted() {
		return deleted;
	}
	public RegistryBank setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}
	public boolean isNotDeleted() {
		return !deleted;
	}
	public void delete() {
		this.deleted = true;
	}
	public void restore() {
		this.deleted = false;
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
	
	public Optional<BankAccount> getBankAccount() {
		return Optional.ofNullable(bankAccount);
	}
	public RegistryBank setBankAccount(BankAccount bankAccount) {
		this.bankAccount = bankAccount;
		return this;
	}
	
	public Optional<Account> getAccount() {
		return Optional.ofNullable(account);
	}
	public RegistryBank setAccount(Account account) {
		this.account = account;
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
	
	public boolean isActive() {
		return active;
	}
	public RegistryBank setActive(boolean active) {
		this.active = active;
		return this;
	}

	public String getRequisition() {
		return requisition;
	}
	public RegistryBank setRequisition(String requisition) {
		this.requisition = requisition;
		return this;
	}
	
	public String getSepaMandateRef() {
		return sepaMandateRef;
	}
	public RegistryBank setSepaMandateRef(String sepaMandateRef) {
		this.sepaMandateRef = sepaMandateRef;
		return this;
	}
	
	public double getBalance() {
		return balance;
	}
	public RegistryBank setBalance(double balance) {
		this.balance = balance;
		return this;
	}
	
	public double getAvailableBalance() {
		return availableBalance;
	}
	public RegistryBank setAvailableBalance(double availableBalance) {
		this.availableBalance = availableBalance;
		return this;
	}
	
	public Date getBalanceDate() {
		return balanceDate;
	}
	public RegistryBank setBalanceDate(Date balanceDate) {
		this.balanceDate = balanceDate;
		return this;
	}
	
}
