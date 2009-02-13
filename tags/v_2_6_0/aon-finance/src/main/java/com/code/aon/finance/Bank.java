package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a Bank.
 * 
 * @author jurkiri
 */
@Entity
@Table(name="bank")
public class Bank implements ITransferObject{
	
	private static final long serialVersionUID = 1084181539066235328L;

	/** The id. */
	private Integer id;
	
	/** The name. */
	private String name;
	
	/** The code. */
	private String code;
	
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
	 * Gets the code.
	 * 
	 * @return the code
	 */
	@Column(length=4, nullable=false)
	public String getCode() {
		return code;
	}

	/**
	 * Sets the code.
	 * 
	 * @param code the code
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(length=64, nullable=false)
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
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return super.equals(obj);
		}
		if (obj instanceof Bank) {
			Bank bank = (Bank) obj;
			if (ObjectUtils.equals(getId(), bank.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
}