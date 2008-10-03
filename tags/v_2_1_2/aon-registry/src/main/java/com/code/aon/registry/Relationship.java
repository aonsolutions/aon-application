package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents types of the relationships between
 * registries.
 * 
 * @author Consulting & Development. ecastellano - 21/02/2007
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "relationship")
public class Relationship implements ITransferObject{

	/** The id. */
	private Integer id;

	/** The name. */
	private String description;

	/**
	 * The empty constructor.
	 */
	public Relationship() {
	}

	/**
	 * The constructor using the id.
	 * 
	 * @param id
	 *            the id
	 */
	public Relationship(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(length = 32, nullable = false)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name
	 */
	public void setDescription(String description) {
		this.description = description;
	}

}