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

	/** The id. */
	private Integer id;
	
	/** The Registry. */
	private Registry registry;
	
    /** The tariff to be applied to the customer. */
    private Tariff tariff;

    /** The surcharge. */
    private boolean surcharge;
    
    /** The withholding. */
    private boolean withholding;

    /** The transaction type. */
    private InvoiceTransactionType transaction;

    /** The status. */
    private CustomerStatus status;
    
    /** The scope. */
    private Scope scope;

    /** Indicates if the customer wants to receive e-Invoice. */
    private boolean eInvoice;    

    /**
     * Gets the id.
     * 
     * @return the id
     */
    @Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
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
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the tariff of the customer.
	 * 
	 * @return the tariff
	 */
    @ManyToOne
    @JoinColumn(name="tariff")
    @ForeignKey(name = "FK_CUSTOMER_TARIFF")
    @Index(name = "IDX_CUSTOMER_TARIFF")        
	public Tariff getTariff() {
		return tariff;
	}
	
	/**
	 * Sets the tariff to the customer.
	 * 
	 * @param tariff the tariff to set.
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
	
    /**
     * Gets if a surcharge has to be applied to the customer or not.
     * 
     * @return true if a surcharge has to be applied.
     */
    @Column(nullable=true)
    public boolean isSurcharge() {
        return surcharge;
    }

    /**
     * Sets if a surcharge has to be applied to the customer or not.
     * 
     * @param surcharge true if a surcharge has to be applied.
     */
    public void setSurcharge(boolean surcharge) {
        this.surcharge = surcharge;
    }

	/**
	 * Checks if a withholding is applied.
	 * 
	 * @return true, if a withholding is applied
	 */
	@Column(nullable=true)
	public boolean isWithholding() {
		return withholding;
	}

	/**
	 * Sets the withholding.
	 * 
	 * @param withholding the withholding
	 */
	public void setWithholding(boolean withholding) {
		this.withholding = withholding;
	}

	/**
	 * Gets the transaction type.
	 * 
	 * @return the transaction type
	 */
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}

	/**
	 * Sets the transaction type.
	 * 
	 * @param transaction the transaction type
	 */
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status
     */
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
	
	/**
	 * Checks if is e invoice.
	 * 
	 * @return true, if is e invoice
	 */
	@Column(name="e_invoice", nullable=true)
	public boolean isEInvoice() {
		return eInvoice;
	}

	/**
	 * Sets the e invoice.
	 * 
	 * @param invoice the new e invoice
	 */
	public void setEInvoice(boolean invoice) {
		eInvoice = invoice;
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