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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_budget_detail")
public class AccountBudgetDetail implements ITransferObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5647680939634585971L;

	/** The id. */
	private Integer id;
	
	/** The account period. */
	private String period;
	
    /** The account. */
    private Account account;

    /** The security level. */
    private SecurityLevel securityLevel;

    /** The entry date. */
    private Date date;

    /** The debit. */
	private Double debit;
	
	/** The credit. */
	private Double credit;
	
	/** The account budget. */
	private AccountBudget accountBudget;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the account period.
	 * 
	 * @return the account period
	 */
	@Column(name="account_period", nullable = false, length = 4)
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_DETAIL_PERIOD_DETAIL")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT_PERIOD")	
	public String getPeriod() {
		return period;
	}

	/**
	 * Sets the account period.
	 * 
	 * @param accountPeriod the account period
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_DETAIL_ACCOUNT_DETAIL")
	@Index(name = "IDX_ACCOUNT_BUDGET_ACCOUNT")
	public Account getAccount() {
		return account;
	}
	
	/**
	 * Sets the account.
	 * 
	 * @param account the account
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	
	/**
	 * Gets the security level.
	 * 
	 * @return the security level
	 */
	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	/**
	 * Sets the security level.
	 * 
	 * @param securityLevel the security level
	 */
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

    /**
     * Gets the entry date.
     * 
     * @return the entry date
     */
	@Temporal(value=TemporalType.DATE)
    @Column(name="entry_date")
    public Date getDate() {
        return date;
    }

    /**
     * Sets the entry date.
     * 
     * @param entryDate the entry date
     */
    public void setDate(Date dateate) {
        this.date = dateate;
    }

    /**
     * Gets the debit.
     * 
     * @return the debit
     */
    public Double getDebit() {
    	if(debit==null)
			return 0.0;
    	return debit;
    }

    /**
     * Sets the debit.
     * 
     * @param debit the debit
     */
    public void setDebit(Double debit) {
        this.debit = debit;
    }

	/**
	 * Gets the credit.
	 * 
	 * @return the credit
	 */
	public Double getCredit() {
		if(credit==null)
			return 0.0;
		return credit;
	}

	/**
	 * Sets the credit.
	 * 
	 * @param credit the credit
	 */
	public void setCredit(Double credit) {
		this.credit = credit;
	}

	/**
	 * Gets the account budget.
	 * 
	 * @return the account budget
	 */
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