package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.enumeration.PayMethodType;

/**
 * Transfer Object that represents an PayMethod.
 */
@Entity
@Table(name = "pay_method")
public class PayMethod implements ITransferObject{
	
	/** The id. */
	private Integer id;
	
	/** The name. */
	private String name;
	
	/** The type of pay method. */
	private PayMethodType type;

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
	 * @param primaryKey the primary key
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name the name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the type of pay method.
	 * 
	 * @return the type
	 */
	@Column(name = "type")
	public PayMethodType getType() {
		return type;
	}

	/**
	 * Sets the type of pay method.
	 * 
	 * @param type the type
	 */
	public void setType(PayMethodType type) {
		this.type = type;
	}
}