package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.finance.Creditor;

@Entity
@Table(name="creditor_account")
public class CreditorAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -1065423464446184552L;

	private Integer id;
	private Creditor creditor;
	private Account account;
	
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="creditor", nullable = false)
	@ForeignKey(name="FK_CREDITOR_ACCOUNT_CREDITOR")
	@Index(name="IDX_CREDITOR_ACCOUNT_CREDITOR")				
	public Creditor getCreditor() {
		return creditor;
	}
	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_CREDITOR_ACCOUNT_ACCOUNT")
	@Index(name="IDX_CREDITOR_ACCOUNT_ACCOUNT")			
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getCreditor();
	}
	public void setLinkedTo(ITransferObject to) {
		setCreditor((Creditor) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getCreditor()==null) ? null : getCreditor().getRegistry().getFullName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CreditorAccount o = (CreditorAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.creditor,o.creditor)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.creditor)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}