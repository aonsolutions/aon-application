package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.common.ITransferObject;

/**
 * Entity class for representing an account.
 * 
 * @author Consulting & Development. ecastellano - 22/01/2007
 * 
 */
@Entity
@Table(name = "amortization_type")
public class AmortizationType implements ITransferObject {

	private static final long serialVersionUID = -4744515826050552526L;

	private Integer id;
	private String description;
    private Account fixedAssetAccount;
    private Account accumulatedAccount;
    private Account allocationAccount;
    private double percentage;

	@Id
    @GeneratedValue	
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the ID of this account.
	 * 
	 * @param id
	 *            The ID of this account.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the description of this account.
	 * 
	 * @return The description of this account
	 */
	@Column(nullable = false, length = 64)
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


	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="fixed_asset_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT")							
	public Account getFixedAssetAccount() {
		return fixedAssetAccount;
	}

	public void setFixedAssetAccount(Account fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="accumulated_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT")						
	public Account getAccumulatedAccount() {
		return accumulatedAccount;
	}

	public void setAccumulatedAccount(Account accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="allocation_account", nullable=false )
	@ForeignKey(name = "FK_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT")
	@Index(name = "IDX_AMORTIZATION_TYPE_ALLOCATION_ACCOUNT")					
	public Account getAllocationAccount() {
		return allocationAccount;
	}

	public void setAllocationAccount(Account allocationAccount) {
		this.allocationAccount = allocationAccount;
	}

	@Column(nullable=true)
    public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof AmortizationType) {
			AmortizationType account = (AmortizationType) obj;
			if (ObjectUtils.equals(getId(), account.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}