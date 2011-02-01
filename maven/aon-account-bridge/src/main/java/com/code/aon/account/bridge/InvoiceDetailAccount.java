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
import com.code.aon.finance.InvoiceDetail;

@Entity
@Table(name="invoice_detail_account")
public class InvoiceDetailAccount implements ITransferObject, IAccount {
	
	private static final long serialVersionUID = -7929315870651403627L;

	private Integer id;
	private InvoiceDetail invoiceDetail;
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
	@JoinColumn(name="invoice_detail", nullable = false)
	@ForeignKey(name="FK_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL")
	@Index(name="IDX_INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL")										
	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}
	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}

	@ManyToOne
	@JoinColumn(name="account", nullable = false)
	@ForeignKey(name="FK_INVOICE_DETAIL_ACCOUNT_ACCOUNT")
	@Index(name="IDX_INVOICE_DETAIL_ACCOUNT_ACCOUNT")									
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}

	@Transient
	public ITransferObject getLinkedTo() {
		return getInvoiceDetail();
	}
	public void setLinkedTo(ITransferObject to) {
		setInvoiceDetail((InvoiceDetail) to);
	}	

	@Transient
	public String getAccountDescription() {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InvoiceDetailAccount o = (InvoiceDetailAccount) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.account,o.account)
				.append(this.invoiceDetail,o.invoiceDetail)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.account)
			.append(this.invoiceDetail)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}