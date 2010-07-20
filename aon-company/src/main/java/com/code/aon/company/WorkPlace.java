package com.code.aon.company;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.enumeration.EconomicAgreement;
import com.code.aon.registry.RegistryAddress;

/**
 * Transfer Object that represents the workPlace.
 * 
 */
@Entity
@Table(name="workplace")
public class WorkPlace implements ITransferObject, IEntity {

	private static final long serialVersionUID = 5078033612665053464L;

	/** Working place identifier */
	private Integer id;

	/** Working place description */
	private String description;
	
	/** Indicates the enterprise that this Working place belongs to */
    private Enterprise enterprise; 	

	/** Working place address */
    private RegistryAddress address;

	/** Economic Agreement */
    private EconomicAgreement economicAgreement;
    
    /** Indicates if the working place is currently active. */
	private boolean active;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
    @Id
	@GeneratedValue
	@Column(nullable=false)
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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(name="description", length = 64, nullable = false)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the enterprise.
	 * 
	 * @return the enterprise
	 */
	@ManyToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false)
    @ForeignKey(name = "FK_WORKPLACE_ENTERPRISE")
    @Index(name = "IDX_WORKPLACE_ENTERPRISE")    
    public Enterprise getEnterprise() {
		return enterprise;
	}

	/**
	 * Sets the enterprise.
	 * 
	 * @param enterprise the new enterprise
	 */
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @JoinColumn(name="address", nullable = false)
    @ForeignKey(name = "FK_WORKPLACE_ADDRESS")
    @Index(name = "IDX_WORKPLACE_ADDRESS")
	public RegistryAddress getAddress() {
		return address;
	}

	/**
	 * Sets the address.
	 * 
	 * @param address the address
	 */
	public void setAddress(RegistryAddress address) {
		this.address = address;
	}

    /**
     * Returns if the employee is currently in the company.
     * 
	 * @return the active
	 */
	@Column(nullable = true)
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	/**
	 * Gets the economic agreement.
	 * 
	 * @return the economic agreement
	 */
	@Column(name="economicAgreement", nullable = true)
	public EconomicAgreement getEconomicAgreement() {
		return economicAgreement;
	}

	/**
	 * Sets the economic agreement.
	 * 
	 * @param economicAgreement the new economic agreement
	 */
	public void setEconomicAgreement(EconomicAgreement economicAgreement) {
		this.economicAgreement = economicAgreement;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.employee.INode#accept(com.code.aon.employee.INodeVisitor)
	 */
	public void accept(IEntityVisitor visitor) {
		visitor.visitWorkPlace( this );
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WorkPlace o = (WorkPlace) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.address, o.address)			
				.append(this.description, o.description)
				.append(this.economicAgreement, o.economicAgreement)
				.append(this.enterprise, o.enterprise)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(address)
			.append(description)
			.append(economicAgreement)
			.append(enterprise)
			.append(id)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
