/*
 * Created on 23-may-2005
 *
 */
package com.code.aon.company;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.domain.DomainFilter;
import com.code.aon.common.domain.IDomain;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name="company")
@PrimaryKeyJoinColumn(name="registry")
public class Company extends Registry implements ITaxInfo, IDomain {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    private int domain;
	private boolean active;    
    private boolean surcharge;
    private boolean withholding;
    private boolean vatAccrualPayment;
    private boolean eInvoice;
    	
    	@DomainFilter
	@Column(name="domain", nullable=false , insertable=false, updatable =false)
	public int getCompanyDomain() {
		return this.domain;
	}

	public void setCompanyDomain(int domain) {
	    this.domain = domain;
	}
	
	@Column(name="domain", nullable=false )
	public int getDomain() {
		return this.domain;
	}

	public void setDomain(int domain) {
	    this.domain = domain;
	}
	
	@Column(nullable=true)
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(nullable=true)
	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}
	
	@Column(nullable=true)
	public boolean isWithholding() {
		return withholding;
	}
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}
	
	@Column(name="vat_accrual_payment", nullable=true)
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public void setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
	}
	
	@Column(name="e_invoice", nullable=true)
	public boolean isEInvoice() {
		return eInvoice;
	}
	public void setEInvoice(boolean invoice) {
		eInvoice = invoice;
	}

	@Transient
	public InvoiceTransactionType getTransaction() {
		return InvoiceTransactionType.NATIONAL;
	}
	
	@Transient
	public boolean isWithholdingFarmer() {
		return false;
	}

	@Transient
	public boolean isVatFree() {
		return false;
	}

	@Transient
	public boolean isRetentionFree() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		final Company o = (Company) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.appendSuper(super.equals(obj))
				.append(this.active, o.active)
				.append(this.domain, o.domain)
				.append(this.eInvoice, o.eInvoice)
				.append(this.surcharge, o.surcharge)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.appendSuper(super.hashCode())
			.append(active)
			.append(domain)
			.append(eInvoice)
			.append(surcharge)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}