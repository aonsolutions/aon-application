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
import com.code.aon.finance.BankConcept;

@Entity
@Table(name="bank_concept_account")
public class BankConceptAccount implements ITransferObject, IAccount {

	private static final long serialVersionUID = 7295096890216509899L;

	private Integer id;
	private BankConcept bankConcept;
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
	@JoinColumn(name="bank_concept", nullable = false)
	@ForeignKey(name="FK_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT")
	@Index(name="IDX_BANK_CONCEPT_ACCOUNT_BANK_CONCEPT")
	public BankConcept getBankConcept() {
		return bankConcept;
	}
	public void setBankConcept(BankConcept bankConcept) {
		this.bankConcept = bankConcept;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_BANK_CONCEPT_ACCOUNT_ACCOUNT")
	@Index(name="IDX_BANK_CONCEPT_ACCOUNT_ACCOUNT")					
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getBankConcept();
	}
	public void setLinkedTo(ITransferObject to) {
		setBankConcept((BankConcept) to);
	}

	@Transient
	public String getAccountDescription() {
		return (getBankConcept()==null) ? null : getBankConcept().getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final BankConceptAccount o = (BankConceptAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.bankConcept,o.bankConcept)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.bankConcept)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}