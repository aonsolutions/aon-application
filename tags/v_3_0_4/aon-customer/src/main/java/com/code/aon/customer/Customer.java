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

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.product.Tariff;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a Customer.
 * 
 * @author Consulting & Development. Inigo Gayarre - 7-sep-2005
 * @since 1.0
 */
@Entity
@Table(name="customer")
public class Customer implements ITransferObject, ITaxInfo, IScopable, IRegistry{
	
	private static final long serialVersionUID = 4701123719465168619L;

	/** The id. */
	private Integer id;
	
	/** The Registry. */
	private Registry registry;
	
    /** The tariff to be applied to the customer. */
    private Tariff tariff;

    /** Indicates if the customer is taxfree or not. */
    private boolean taxFree;

    /** Indicates if a surcharge has to be applied to the customer. */
    private boolean surcharge;
    
    /** The withholding. */
    private boolean withholding;

    /** The status of the customer. */
    private CustomerStatus status;
    
    /** The segment. */
    private CustomerSegment customerSegment;
    
    private Scope scope;

    /**
     * Gets the id.
     * 
     * @return Returns the id
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
     * @param id The id
     */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the registry.
	 * 
	 * @return Returns the registry.
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
	 * @param registry the Registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the status of the customer.
	 * 
	 * @return The status of the customer.
	 */
    public CustomerStatus getStatus() {
        return status;
    }

    /**
     * Sets the status of the customer.
     * 
     * @param status The status of the customer.
     */
    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    /**
     * Gets if a surcharge has to be applied to the customer or not.
     * 
     * @return True if a surcharge has to be applied.
     */
    public boolean isSurcharge() {
        return surcharge;
    }

    /**
     * Sets if a surcharge has to be applied to the customer or not.
     * 
     * @param surcharge True if a surcharge has to be applied.
     */
    public void setSurcharge(boolean surcharge) {
        this.surcharge = surcharge;
    }

    /**
     * Gets if taxes has to be applied to the customer or not.
     * 
     * @return True if taxes has to be applied to the customer.
     */
    public boolean isTaxFree() {
        return taxFree;
    }

    /**
     * Sets if taxes has to be applied to the customer or not.
     * 
     * @param taxFree True if taxes has to be applied to the customer or not.
     */
    @Column(name="taxfree")
    public void setTaxFree(boolean taxFree) {
        this.taxFree = taxFree;
    }
    
	/**
	 * Gets the tariff of the customer.
	 * 
	 * @return Returns the tariff.
	 */
    @ManyToOne
    @JoinColumn(name="tariff")
	public Tariff getTariff() {
		return tariff;
	}
	
	/**
	 * Sets the tariff to the customer.
	 * 
	 * @param tariff The tariff to set.
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
	
	/**
	 * Checks if a withholding is applied.
	 * 
	 * @return true, if a withholding is applied
	 */
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
	 * Gets the segment.
	 * 
	 * @return the segment
	 */
	@ManyToOne
    @JoinColumn(name="segment")
	public CustomerSegment getCustomerSegment() {
		return customerSegment;
	}

	/**
	 * Sets the segment.
	 * 
	 * @param segment the segment
	 */
	public void setCustomerSegment(CustomerSegment customerSegment) {
		this.customerSegment = customerSegment;
	}
	
	@ManyToOne
    @JoinColumn(name="scope", nullable=false)
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Customer) {
			Customer o = (Customer) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}