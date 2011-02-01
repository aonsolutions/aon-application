package com.code.aon.account.bridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.enumeration.TaxAccountType;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.Tax;

@Entity
@Table(name="tax_account")
public class TaxAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = 1809456459170682443L;

	private Integer id;
	private Tax tax;
	private Account account;
	private TaxAccountType type;

	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="tax", nullable = false)
	@ForeignKey(name="FK_TAX_ACCOUNT_TAX")
	@Index(name="IDX_TAX_ACCOUNT_TAX")						
	public Tax getTax() {
		return tax;
	}
	public void setTax(Tax tax) {
		this.tax = tax;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_TAX_ACCOUNT_ACCOUNT")
	@Index(name="IDX_TAX_ACCOUNT_ACCOUNT")					
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Column(nullable=false)
	public TaxAccountType getType() {
		return type;
	}
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
		return (getTax()==null) ? null : getTax().getName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TaxAccount o = (TaxAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.tax,o.tax)
				.append(this.type,o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.tax)
			.append(this.type)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}