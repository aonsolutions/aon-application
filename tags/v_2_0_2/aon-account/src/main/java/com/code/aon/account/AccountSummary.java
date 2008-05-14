package com.code.aon.account;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents an AccountEntryDetail.
 */
@Entity
@Table(name = "account_summary")
public class AccountSummary implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The account period. */
	private String accountPeriod;
	
    /** The account. */
    private Account account;

    /** The entry date. */
    private Date entryDate;

    /** The entry month. */
    private int entryMonth;

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
	@Column(name="account_period", length=4)
	public String getAccountPeriod() {
		return accountPeriod;
	}

	/**
	 * Sets the account period.
	 * 
	 * @param accountPeriod the account period
	 */
	public void setAccountPeriod(String accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="account", nullable=false )
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
     * Gets the entry date.
     * 
     * @return the entry date
     */
    @Column(name="entry_date")
    public Date getEntryDate() {
        return entryDate;
    }

    /**
     * Sets the entry date.
     * 
     * @param entryDate the entry date
     */
    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    /**
     * Gets the entry month.
     * 
     * @return the entry month
     */
    @Column(name="entry_month")
    public int getEntryMonth() {
        return entryMonth;
    }

    /**
     * Sets the entry month.
     * 
     * @param entryMonth the entry month
     */
    public void setEntryMonth(int entryMonth) {
        this.entryMonth = entryMonth;
    }

    /**
     * Gets the debit.
     * 
     * @return the debit
     */
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