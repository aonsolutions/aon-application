package com.code.aon.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Formula;

import com.code.aon.common.ITransferObject;

/**
 * Entity class for representing an account.
 * 
 * @author Consulting & Development. ecastellano - 22/01/2007
 * 
 */
@Entity
@Table(name = "account")
public class Account implements ITransferObject {

	/**
	 * The ID of this account
	 */
	private String id;

	/**
	 * The description of the account.
	 */
	private String description;

	/**
	 * A meaningful alias for this account.
	 */
	private String alias;

	/**
	 * TRUE if account entries are enabled, FALSE otherwise.
	 */
	private boolean entryEnabled;

	/**
	 * Level of this account.
	 */
	private int level;

	/**
	 * Gets the ID of this account.
	 * 
	 * @return The ID of this account
	 */
	@Id
	@Column(nullable = false)
	public String getId() {
		return id;
	}

	/**
	 * Sets the ID of this account.
	 * 
	 * @param id
	 *            The ID of this account.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Gets the description of this account.
	 * 
	 * @return The description of this account
	 */
	@Column(nullable = false, length = 100)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this account.
	 * 
	 * @param description
	 *            The description of this account.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the alias of this account.
	 * 
	 * @return The alias of this account
	 */
	@Column(length = 15)
	public String getAlias() {
		return alias;
	}

	/**
	 * Sets the alias of this account.
	 * 
	 * @param alias
	 *            The alias of this account.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Gets if account entries are enabled.
	 * 
	 * @return <code>true</code> if account entries are enabled,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEntryEnabled() {
		return entryEnabled;
	}

	/**
	 * Sets if account entries are enabled.
	 * 
	 * @param entryEnabled
	 *            <code>true</code> if account entries are enabled,
	 *            <code>false</code> otherwise.
	 */
	public void setEntryEnabled(boolean entryEnabled) {
		this.entryEnabled = entryEnabled;
	}

	/**
	 * Returns the level of this account.
	 * 
	 * @return The level of this account.
	 */
	@Formula("LENGTH(id)")
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the level of this account.
	 * 
	 * @param level
	 *            The level of this account.
	 * 
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
    public boolean equals(Object obj) {
        return this.id.equals(((Account)obj).getId()); 
    }
}