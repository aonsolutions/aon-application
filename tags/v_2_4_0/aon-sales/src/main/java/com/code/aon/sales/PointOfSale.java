package com.code.aon.sales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.RegistryAddress;

/**
 * Transfer Object that represents a Point of sale.
 */
@Entity
@Table(name="pos")
public class PointOfSale implements ITransferObject {
	
	/** The id. */
	private Integer id;
	
	/** The description. */
	private String description;
	
	/** One of the addresses of the company. */
	private RegistryAddress rAddress;

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
	@Column(nullable=false, length=128)
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
	 * Gets the rAddress.
	 * 
	 * @return the rAddress
	 */
	@ManyToOne
	@JoinColumn( name="raddress", nullable = false)
	public RegistryAddress getRAddress() {
		return rAddress;
	}

	/**
	 * Sets the rAddress.
	 * 
	 * @param address rAddress
	 */
	public void setRAddress(RegistryAddress address) {
		rAddress = address;
	}
	
	
}