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
import com.code.aon.finance.FinanceBatch;

@Entity
@Table(name="account_entry_fbatch")
public class AccountEntryFinanceBatch implements ITransferObject {

	private static final long serialVersionUID = 7131853142761580207L;

	private Integer id;
	private AccountEntry accountEntry;
	private FinanceBatch financeBatch;

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
	@JoinColumn(name="account_entry", nullable = false)
	@ForeignKey(name="FK_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY")
	@Index(name="IDX_ACCOUNT_ENTRY_FBATCH_ACCOUNT_ENTRY")
	public AccountEntry getAccountEntry() {
		return accountEntry;
	}
	public void setAccountEntry(AccountEntry accountEntry) {
		this.accountEntry = accountEntry;
	}

	@ManyToOne
	@JoinColumn(name="fbatch", nullable = false)
	@ForeignKey(name="FK_ACCOUNT_ENTRY_FBATCH_FBATCH")
	@Index(name="IDX_ACCOUNT_ENTRY_FBATCH_FBATCH")
	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}
	public void setFinanceBatch(FinanceBatch financeBatch) {
		this.financeBatch = financeBatch;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AccountEntryFinanceBatch o = (AccountEntryFinanceBatch) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.accountEntry,o.accountEntry)
				.append(this.financeBatch,o.financeBatch)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.accountEntry)
			.append(this.financeBatch)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}