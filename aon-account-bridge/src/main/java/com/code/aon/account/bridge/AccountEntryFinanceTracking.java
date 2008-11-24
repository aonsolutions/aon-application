package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.FinanceTracking;

@Entity
@Table(name="account_entry_finance_tracking")
public class AccountEntryFinanceTracking implements ITransferObject {

	private static final long serialVersionUID = -7572383957474505739L;

	private Integer id;
	
	private AccountEntry accountEntry;
	
	private FinanceTracking financeTracking;


	@Id
	@Column(nullable=false)
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn( name="account_entry", nullable = false)
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}

	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	@ManyToOne
	@JoinColumn( name="finance_tracking", nullable = false)
	public FinanceTracking getFinanceTracking() {
		return financeTracking;
	}

	public void setFinanceTracking(FinanceTracking financeTracking) {
		this.financeTracking = financeTracking;
	}
}