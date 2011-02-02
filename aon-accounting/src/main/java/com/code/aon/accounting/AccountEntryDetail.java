package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_entry_detail")
public class AccountEntryDetail implements ITransferObject {

	private static final long serialVersionUID = -1771463579504113080L;

	private Integer id;
	private AccountEntry accountEntry;
	private int line;
	private Account account;
	private String concept;
	private Account balancingAccount;
	private double debit;
	private double credit;
	private String documentNumber;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account_entry", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY")
	@Index(name = "IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY")			
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	public int getLine() {
		return line;
	}
	public void setLine(int line) {
		this.line = line;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_DETAIL_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_ENTRY_DETAIL_ACCOUNT")	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(length=32)
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="balancing_account")
	@ForeignKey(name = "FK_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT")
	@Index(name = "IDX_ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT")		
	public Account getBalancingAccount() {
		return balancingAccount;
	}
	public void setBalancingAccount(Account balancingAccount) {
		this.balancingAccount = balancingAccount;
	}

	@Column(nullable=true)
	public double getDebit() {
		return debit;
	}
	public void setDebit(double debit) {
		if (debit != 0) {
			if (debit < 0) {
				setCredit(CommonUtil.round(0-debit));
				debit = 0;
			} else {
				setCredit(0);
			}
		}
		this.debit = debit;
	}

	@Column(nullable=true)
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		if (credit != 0) {
			if (credit < 0) {
				setDebit(CommonUtil.round(0-credit));
				credit = 0;
			} else {
				setDebit(0);
			}
		}
		this.credit = credit;
	}


	@Column(name="document_number",length=32)
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	@Transient
	public double getUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return CommonUtil.round(getDebit() - getCredit());
		}
		return 0;
	}
	
	@Transient
	public double getCreditBalance() {
		if (getCredit() > getDebit()) {
			return CommonUtil.round(getCredit() - getDebit());
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AccountEntryDetail o = (AccountEntryDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.accountEntry,o.accountEntry)
				.append(this.line,o.line)		
				.append(this.account,o.account)		
				.append(this.concept,o.concept)
				.append(this.balancingAccount,o.balancingAccount)
				.append(this.debit,o.debit)
				.append(this.credit,o.credit)
				.append(this.documentNumber,o.documentNumber)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.accountEntry)
			.append(this.line)		
			.append(this.account)		
			.append(this.concept)
			.append(this.balancingAccount)
			.append(this.debit)
			.append(this.credit)
			.append(this.documentNumber)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}