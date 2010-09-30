package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.Tax;

/**
 * The Class TaxAccount.
 */
@Entity
@Table(name="tax_account")
public class TaxAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = 1809456459170682443L;

	/** The id. */
	private Integer id;
	
	/** The tax. */
	private Tax tax;
	
	/** The account. */
	private Account account;
	
	/** The type. */
	private TaxAccountType type;

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
	 * Gets the tax.
	 * 
	 * @return the tax
	 */
	@ManyToOne
	@JoinColumn(name="tax", nullable = false)
	@ForeignKey(name="FK_TAX_ACCOUNT_TAX")
	@Index(name="IDX_TAX_ACCOUNT_TAX")						
	public Tax getTax() {
		return tax;
	}

	/**
	 * Sets the tax.
	 * 
	 * @param tax the tax
	 */
	public void setTax(Tax tax) {
		this.tax = tax;
	}

	/**
	 * Gets the account.
	 * 
	 * @return the account
	 */
	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_TAX_ACCOUNT_ACCOUNT")
	@Index(name="IDX_TAX_ACCOUNT_ACCOUNT")					
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(nullable=false)
	public TaxAccountType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(TaxAccountType type) {
		this.type = type;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getTax();
	}
	public void setLinkedTo(ITransferObject to) {
		setTax((Tax) to);
	}

	@Transient
	public String getAccountDescription() {
		return getTax()==null?null:getTax().getName();
	}
}