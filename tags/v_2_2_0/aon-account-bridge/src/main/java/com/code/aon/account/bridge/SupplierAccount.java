package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;
import com.code.aon.supplier.Supplier;

/**
 * The Class SupplierAccount.
 */
@Entity
@Table(name="supplier_account")
public class SupplierAccount implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The supplier. */
	private Supplier supplier;
	
	/** The account. */
	private Account account;
	
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
	 * Gets the supplier.
	 * 
	 * @return the supplier
	 */
	@ManyToOne
	@JoinColumn( name="supplier", nullable = false)
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * Sets the supplier.
	 * 
	 * @param supplier the supplier
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne
	@JoinColumn( name="account", nullable = false)
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
}