package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.BankStatement;

@Entity
@Table(name="account_entry_bank_statement")
public class AccountEntryBankStatement implements ITransferObject {

	private static final long serialVersionUID = 2623167813752953193L;

	private Integer id;
	private AccountEntry accountEntry;
	private BankStatement bankStatement;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="account_entry", nullable = false)
	@ForeignKey(name="FK_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY")
	@Index(name="IDX_ACC_ENTRY_BANK_STATEMENT_ACC_ENTRY")
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}
	
	@ManyToOne
	@JoinColumn(name="bank_statement", nullable = false)
	@ForeignKey(name="FK_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT")
	@Index(name="IDX_ACC_ENTRY_BANK_STATEMENT_BANK_STATEMENT")
	public BankStatement getBankStatement() {
		return bankStatement;
	}
	public void setBankStatement(BankStatement bankStatement) {
		this.bankStatement = bankStatement;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AccountEntryBankStatement o = (AccountEntryBankStatement) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.accountEntry,o.accountEntry)
				.append(this.bankStatement,o.bankStatement)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.accountEntry)
			.append(this.bankStatement)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}