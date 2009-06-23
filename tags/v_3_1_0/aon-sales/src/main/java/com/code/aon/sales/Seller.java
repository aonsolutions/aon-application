package com.code.aon.sales;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.sales.enumeration.SellerStatus;

// TODO: Auto-generated Javadoc
/**
 * Transfer Object that represents a seller.
 * 
 * @author Consulting & Development. Inigo Gayarre - 12-sep-2005
 * @version 1.0
 */
@Entity
@Table(name="seller")
public class Seller implements ITransferObject, IRegistry {

	private static final long serialVersionUID = -5570727365136434306L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The description. */
	private String description;

	/** The status. */
	private SellerStatus status;

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
	 * Gets the description.
	 * 
	 * @return the description
	 */
	@Column(length=64)
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
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public SellerStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(SellerStatus status) {
		this.status = status;
	}

}