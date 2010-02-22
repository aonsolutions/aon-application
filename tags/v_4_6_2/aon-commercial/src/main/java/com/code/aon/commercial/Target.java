package com.code.aon.commercial;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;

/**
 * Transfer Object that represents a Target.
 */
@Entity
@Table(name="target")
@PrimaryKeyJoinColumn(name="registry")
public class Target implements ITransferObject, ITaxInfo, IRegistry {
	
	private static final long serialVersionUID = -7492435795404962788L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The advertising. */
	private Advertising advertising;

    /** The surcharge. */
    private boolean surcharge;
    
    /** The withholding. */
    private boolean withholding;

    /** The transaction type. */
    private InvoiceTransactionType transaction;

    /** The status. */
    private TargetStatus status;

	/** The items. */
	private Set<TargetItem> items = new HashSet<TargetItem>();

	/** The sellers. */
	private Set<TargetSeller> sellers = new HashSet<TargetSeller>();

	/** The sellers. */
	private Set<CommercialTracking> trackings = new HashSet<CommercialTracking>();
	
	/**
	 * The empty onstructor.
	 */
	public Target(){
		this.registry = new Registry();
	}
	
	/**
	 * The constructor using a Registry.
	 * 
	 * @param registry the registry
	 */
	public Target(Registry registry) {
		this.registry = registry;
		this.id = registry.getId();
	}

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
	 * Gets the advertising.
	 * 
	 * @return the advertising
	 */
	@Column(nullable=false)
	public Advertising getAdvertising() {
		return advertising;
	}

	/**
	 * Sets the advertising.
	 * 
	 * @param advertising the new advertising
	 */
	public void setAdvertising(Advertising advertising) {
		this.advertising = advertising;
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
    public TargetStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * 
     * @param status the status
     */
    public void setStatus(TargetStatus status) {
        this.status = status;
    }

	/**
	 * Gets the items.
	 * 
	 * @return the items
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetItem> getItems() {
		return items;
	}

	/**
	 * Sets the items.
	 * 
	 * @param items the new items
	 */
	public void setItems(Set<TargetItem> items) {
		this.items = items;
	}

	/**
	 * Gets the sellers.
	 * 
	 * @return the sellers
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})	
	public Set<TargetSeller> getSellers() {
		return sellers;
	}

	/**
	 * Sets the sellers.
	 * 
	 * @param sellers the new sellers
	 */
	public void setSellers(Set<TargetSeller> sellers) {
		this.sellers = sellers;
	}

	/**
	 * Gets the trackings.
	 * 
	 * @return the trackings
	 */
	@OneToMany(mappedBy = "target", cascade={CascadeType.REMOVE})
	public Set<CommercialTracking> getTrackings() {
		return trackings;
	}

	/**
	 * Sets the trackings.
	 * 
	 * @param trackings the new trackings
	 */
	public void setTrackings(Set<CommercialTracking> trackings) {
		this.trackings = trackings;
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
		final Target o = (Target) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.advertising, o.advertising)
				.append(this.registry, o.registry)
				.append(this.surcharge, o.surcharge)
				.append(this.transaction, o.transaction)
				.append(this.withholding, o.withholding)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(advertising)
			.append(id)
			.append(registry)			
			.append(surcharge)
			.append(transaction)
			.append(withholding)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}