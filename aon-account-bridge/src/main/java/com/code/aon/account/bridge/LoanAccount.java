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
import com.code.aon.accounting.Loan;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="loan_account")
public class LoanAccount implements ITransferObject, IAccount {

	private static final long serialVersionUID = 4415920212702994723L;

	private Integer id;
	private Loan loan;
	private Account account;

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
	@JoinColumn(name="loan", nullable = false)
	@ForeignKey(name="FK_LOAN_ACCOUNT_LOAN")
	@Index(name="IDX_LOAN_ACCOUNT_LOAN")								
	public Loan getLoan() {
		return loan;
	}
	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_LOAN_ACCOUNT_ACCOUNT")
	@Index(name="IDX_LOAN_ACCOUNT_ACCOUNT")							
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Transient
	public ITransferObject getLinkedTo() {
		return getLoan();
	}
	public void setLinkedTo(ITransferObject to) {
		setLoan((Loan) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getLoan()==null) ? null : getLoan().getDescription();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final LoanAccount o = (LoanAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.loan,o.loan)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.loan)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}