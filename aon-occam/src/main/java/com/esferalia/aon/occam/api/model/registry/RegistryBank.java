package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.finance.BankAccount;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.AonUtils;

public class RegistryBank implements Serializable {
	
	private static final long serialVersionUID = 3208247929297185256L;
	
	private Integer id;
	private Integer domain;
	private Integer registry;
	private BankAccount bankAccount;
	private String bic;
	private String suffix;
	private String alias;
	private Account account;
	private String requisition;
	private String sepaMandateRef;
	
	private Double balance;
	private Double avaibleBalance;
	private Date balanceDate;
	
	private Boolean active;
	private boolean dirty;
	private boolean removed;
		
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
		if(bankAccount == null) 
			bankAccount = new BankAccount();
		return bankAccount;
	}
	public RegistryBank setBankAccount(BankAccount bankAccount) {
		this.setDirty(true);
		this.bankAccount = bankAccount;
		return this;
	}
	
	public Account getAccount() {
		if(account == null) {
			account = new Account();
		}
		return account;
	}
	public RegistryBank setAccount(Account account) {
		this.setDirty(true);
		this.account = account;
		return this;
	}
	
	@Deprecated
	public Integer getAccountId() {
		return getAccount().getId();
	}
	
	@Deprecated
	public RegistryBank setAccountId(Integer accountId) {
		this.setDirty(true);
		getAccount().setId(accountId);
		return this;
	}
	
	@Deprecated
	public String getAccountCode() {
		return getAccount().getCode();
	}
	
	@Deprecated
	public RegistryBank setAccountCode(String accountCode) {
		getAccount().setCode(accountCode);
		return this;
	}

	@Deprecated
	public String getAccountDescription() {
		return getAccount().getDescription();
	}
	
	@Deprecated
	public RegistryBank setAccountDescription(String accountDescription) {
		getAccount().setDescription(accountDescription);
		return this;
	}

	public String getBic() {
		return bic;
	}
	public RegistryBank setBic(String bic) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.bic , bic));
		this.bic = bic;
		return this;
	}
	public String getSuffix() {
		return suffix;
	}
	public RegistryBank setSuffix(String suffix) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.suffix, suffix));
		this.suffix = suffix;
		return this;
	}
	public String getAlias() {
		return alias;
	}
	public RegistryBank setAlias(String alias) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.alias, alias));
		this.alias = alias;
		return this;
	}
	
	public String getRequisition() {
		return requisition;
	}
	public RegistryBank setRequisition(String requisition) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.requisition , requisition));
		this.requisition = requisition;
		return this;
	}
	
	public String getSepaMandateRef() {
		return sepaMandateRef;
	}
	public RegistryBank setSepaMandateRef(String sepaMandateRef) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.sepaMandateRef, sepaMandateRef));
		this.sepaMandateRef = sepaMandateRef;
		return this;
	}
	
	public Double getBalance() {
		return balance;
	}
	
	public RegistryBank setBalance(Double balance) {
		this.balance = balance;
		return this;
	}
	
	public Double getAvaibleBalance() {
		return avaibleBalance;
	}
	
	public RegistryBank setAvaibleBalance(Double avaibleBalance) {
		this.avaibleBalance = avaibleBalance;
		return this;
	}
	
	public Date getBalanceDate() {
		return balanceDate;
	}
	
	public RegistryBank setBalanceDate(Date balanceDate) {
		this.balanceDate = balanceDate;
		return this;
	}
	
	public Byte getActive() {
		return (byte) (active ? 1 : 0);
	}
	
	public Boolean isActive() {
		return active;
	}
	
	public RegistryBank setActive(Boolean active) {
		this.setDirty(isDirty()?true:AonUtils.notEquals(this.active, active));
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
	
	public boolean isDirty() {
		return dirty;
	}
	
	public RegistryBank setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public RegistryBank setRemoved(boolean removed) {
		this.removed = removed;
		return this;
	}
	
	public void remove() {
		setRemoved(true);
	}
	
	public boolean isEmpty() {
	    return getId() == null && getDomain() == null 
	            && getRegistry() == null;
	}
}
