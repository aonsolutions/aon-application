package com.code.aon.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

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

	private static final long serialVersionUID = -4744515826050552526L;

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

	private int level;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Column(nullable = false, length = 12)
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
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
	@Column(nullable = false, length = 128)
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
	@Column(length = 32)
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
	@Column(nullable = true)
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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the level of this account.
	 * 
	 * @return The level of this account.
	 */
	@Transient
	public String getFullDescription() {
		return (getId() + " " + getDescription());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final Account o = (Account) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.description, o.description)
			.append(this.entryEnabled, o.entryEnabled)
			.append(this.alias, o.alias)
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.description)
			.append(this.entryEnabled)
			.append(this.alias)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}