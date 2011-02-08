package com.code.aon.planner.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="incidence_type")
public class IncidenceType implements ITransferObject {
	
	/** Identifier */
	private Integer id;
	
	/** Abbreviation */
	private String alias;
	
	/** Description */
	private String description;

	/** This field tells if this type of incidence compute as Equal, Minus or Plus. */
	private int compute;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the identifier to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the alias
	 */
	@Column(length=3)
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the description
	 */
	@Column(length=32)
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the compute
	 */
	public int getCompute() {
		return compute;
	}

	/**
	 * @param compute the compute to set
	 */
	public void setCompute(int compute) {
		this.compute = compute;
	}

}
