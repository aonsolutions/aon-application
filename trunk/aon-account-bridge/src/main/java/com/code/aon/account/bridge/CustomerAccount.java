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
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.customer.Customer;

@Entity
@Table(name="customer_account")
public class CustomerAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = 5965646601798660054L;

	private Integer id;
	private Customer customer;
	private Account account;
	
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
	@JoinColumn(name="customer", nullable = false)
	@ForeignKey(name="FK_CUSTOMER_ACCOUNT_CUSTOMER")
	@Index(name="IDX_CUSTOMER_ACCOUNT_CUSTOMER")
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_CUSTOMER_ACCOUNT_ACCOUNT")
	@Index(name="IDX_CUSTOMER_ACCOUNT_ACCOUNT")	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getCustomer();
	}
	public void setLinkedTo(ITransferObject to) {
		setCustomer((Customer) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getCustomer()==null) ? null : getCustomer().getRegistry().getFullName();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CustomerAccount o = (CustomerAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.customer,o.customer)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.customer)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}