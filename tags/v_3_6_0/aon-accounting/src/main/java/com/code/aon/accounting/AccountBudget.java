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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_budget")
public class AccountBudget implements ITransferObject {
	
	private static final long serialVersionUID = -8220548741479656597L;

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
	private double debit;
	
	/** The credit. */
	private double credit;

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
	@Column(name="account_period", length=4, nullable=false)
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
	@ForeignKey(name = "FK_ACCOUNT_BUDGET_ACCOUNT")
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
    @Column(name="entry_date")
    @Temporal(TemporalType.DATE)
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
    @Column(nullable=true)
    public double getDebit() {
        return debit;
    }

    /**
     * Sets the debit.
     * 
     * @param debit the debit
     */
    public void setDebit(double debit) {
        this.debit = debit;
    }

	/**
	 * Gets the credit.
	 * 
	 * @return the credit
	 */
    @Column(nullable=true)
	public double getCredit() {
		return credit;
	}

	/**
	 * Sets the credit.
	 * 
	 * @param credit the credit
	 */
	public void setCredit(double credit) {
		this.credit = credit;
	}
}