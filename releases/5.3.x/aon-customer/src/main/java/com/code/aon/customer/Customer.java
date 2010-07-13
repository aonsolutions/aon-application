package com.code.aon.customer;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.config.Tariff;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a Customer.
 */
@Entity
@Table(name="customer")
@PrimaryKeyJoinColumn(name="registry")
public class Customer implements ITransferObject, ITaxInfo, IScopable, IRegistry {
	
	private static final long serialVersionUID = 4701123719465168619L;

	private Integer id;
	private Registry registry;
    private Tariff tariff;
    private boolean surcharge;
    private boolean withholding;
    private InvoiceTransactionType transaction;
    private CustomerStatus status;
    private Scope scope;
    private boolean eInvoice;
    private boolean deliveryGrouped = true;
    private boolean deliveryValuated = true;

    @Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

    @ManyToOne
    @JoinColumn(name="tariff")
    @ForeignKey(name = "FK_CUSTOMER_TARIFF")
    @Index(name = "IDX_CUSTOMER_TARIFF")        
	public Tariff getTariff() {
		return tariff;
	}
	
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
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

	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

    public CustomerStatus getStatus() {
        return status;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

	@ManyToOne
    @JoinColumn(name="scope", nullable=false)
    @ForeignKey(name = "FK_CUSTOMER_SCOPE")
    @Index(name = "IDX_CUSTOMER_SCOPE")
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}
	
	@Column(name="e_invoice", nullable=true)
	public boolean isEInvoice() {
		return eInvoice;
	}

	public void setEInvoice(boolean eInvoice) {
		this.eInvoice = eInvoice;
	}
	
	@Column(name="delivery_grouped", nullable=true)
	public boolean isDeliveryGrouped() {
		return deliveryGrouped;
	}

	public void setDeliveryGrouped(boolean deliveryGrouped) {
		this.deliveryGrouped = deliveryGrouped;
	}
	
	@Column(name="delivery_valuated", nullable=true)
	public boolean isDeliveryValuated() {
		return deliveryValuated;
	}

	public void setDeliveryValuated(boolean deliveryValuated) {
		this.deliveryValuated = deliveryValuated;
	}
	
	@Transient
	public boolean isTaxFree() {
		return (getTransaction() != InvoiceTransactionType.NATIONAL);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Customer o = (Customer) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.deliveryGrouped, o.deliveryGrouped)
				.append(this.deliveryValuated, o.deliveryValuated)
				.append(this.eInvoice, o.eInvoice)
				.append(this.registry, o.registry)
				.append(this.scope, o.scope)
				.append(this.status, o.status)
				.append(this.surcharge, o.surcharge)
				.append(this.tariff, o.tariff)
				.append(this.transaction, o.transaction)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(deliveryGrouped)
			.append(deliveryValuated)
			.append(eInvoice)
			.append(id)
			.append(registry)
			.append(scope)
			.append(status)
			.append(surcharge)
			.append(tariff)
			.append(transaction)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}