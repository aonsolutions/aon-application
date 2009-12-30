package com.code.aon.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Index;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

/**
 * TransferObject that represents an AccountEntry.
 */
@Entity
@Table(name = "account_entry")
public class AccountEntry implements ITransferObject {
	
	private static final long serialVersionUID = -3297371099219203320L;

	/** The id. */
	private Integer id;
	
	/** The period. */
	private String accountPeriod;
	
	/** The entry date. */
	private Date entryDate;
	
	/** The account entry type. */
	private AccountEntryType type;
	
	/** The journal. */
	private Integer journal;
	
	/** The security level. */
	private SecurityLevel securityLevel;
	

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
	@Index(name = "IDX_ACCOUNT_ENTRY_ACCOUNT_PERIOD")
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
	 * Gets the entry date.
	 * 
	 * @return the entry date
	 */
	@Column(name="entry_date")
	@Temporal(value=TemporalType.DATE)
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(name="entry_type")
	public AccountEntryType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(AccountEntryType type) {
		this.type = type;
	}

	/**
	 * Gets the journal.
	 * 
	 * @return the journal
	 */
	public Integer getJournal() {
		return journal;
	}

	/**
	 * Sets the journal.
	 * 
	 * @param journal the journal
	 */
	public void setJournal(Integer journal) {
		this.journal = journal;
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

    @Override
    public boolean equals(Object obj) {
        return this.id.equals(((AccountEntry)obj).getId()); 
    }

}