package com.code.aon.accounting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;

/**
 * TransferObject that represents a BalanceDetail.
 */
@Entity
@Table(name = "balance_detail")
public class BalanceDetail implements ITransferObject {
		
	private static final long serialVersionUID = -7813781883946689430L;
	
	private Integer id;	
	private Balance balance;
	private String  code;
	private String  description;
	private String  accounts;
	private Integer sortKey;
	private boolean title;
	private boolean internalCalculation;
	private boolean visible;
	private boolean zeroFlag;
	private boolean creditNature;
    


	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="balance")	
	@ForeignKey(name = "FK_BALANCE_DETAIL_BALANCE")
	@Index(name = "IDX_BALANCE_DETAIL_BALANCE")
	public Balance getBalance() {
		return balance;
	}

	public void setBalance(Balance balance) {
		this.balance = balance;
	}
		
	@Column(name="code", length=16)
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name="description", length=128)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Lob
	@Type(type="stringClob")
	public String getAccounts() {
		return accounts;
	}

	public void setAccounts(String accounts) {
		this.accounts = accounts;
	}

	@Column(name="sortKey")
	public Integer getSortKey() {
		return sortKey;
	}

	public void setSortKey(Integer sortKey) {
		this.sortKey = sortKey;
	}
	
	@Column(name="title")
	public boolean isTitle() {
		return title;
	}

	public void setTitle(boolean title) {
		this.title = title;
	}
	
	@Column(name="internal_calculation")
	public boolean isInternalCalculation() {
		return internalCalculation;
	}

	public void setInternalCalculation(boolean internalCalculation) {
		this.internalCalculation = internalCalculation;
	}
	
	@Column(name="visible")
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	@Column(name="zeroFlag")
	public boolean isZeroFlag() {
		return zeroFlag;
	}

	public void setZeroFlag(boolean zeroFlag) {
		this.zeroFlag = zeroFlag;
	}	
	
	@Column(name="creditNature")
	public boolean isCreditNature() {
		return creditNature;
	}
			
	public void setCreditNature(boolean creditNature) {
		this.creditNature = creditNature;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() !=  getClass()) return false;
		final BalanceDetail o = (BalanceDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.getCode(), o.getCode())
			.append(this.getAccounts(), o.getAccounts())
			.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.getId())
			.append(this.getCode())
			.append(this.getAccounts())
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("id",this.getId())
			.append("code",this.getCode())
			.append("accounts",this.getAccounts()).toString();
	}
}