package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_budget_detail")
public class AccountBudgetDetail implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = 5647680939634585971L;

	private Integer id;
	private String period;
    private Account account;
    private SecurityLevel securityLevel;
    private Date date;
	private Double debit;
	private Double credit;
	private AccountBudget accountBudget;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="account_period", nullable = false, length = 4)
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_DETAIL_PERIOD_DETAIL")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT_PERIOD")	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_DETAIL_ACCOUNT_DETAIL")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT")
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Transient
	@Override
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	@Override
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Temporal(value=TemporalType.DATE)
    @Column(name="entry_date")
    public Date getDate() {
        return date;
    }
    public void setDate(Date dateate) {
        this.date = dateate;
    }

    public Double getDebit() {
    	if(debit==null)
			return 0.0;
    	return debit;
    }
    public void setDebit(Double debit) {
        this.debit = debit;
    }

    public Double getCredit() {
		if(credit==null)
			return 0.0;
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account_budget", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_DETAIL_ACCOUNT_BUDGET")
	@Index(name = "IDX_ACCOUNT_BUDGET")
	public AccountBudget getAccountBudget() {
		return accountBudget;
	}
	public void setAccountBudget(AccountBudget accountBudget) {
		this.accountBudget = accountBudget;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final AccountBudgetDetail o = (AccountBudgetDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.period, o.period)
			.append(this.account, o.account)
			.append(this.securityLevel, o.securityLevel)
			.append(this.date, o.date)
			.append(this.debit, o.debit)
			.append(this.credit, o.credit)
			.append(this.accountBudget, o.accountBudget)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.period)
			.append(this.account)
			.append(this.securityLevel)
			.append(this.date)
			.append(this.debit)
			.append(this.credit)
			.append(this.accountBudget)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}