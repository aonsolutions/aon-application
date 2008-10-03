package com.code.aon.tas;

// Generated 03-ago-2006 10:16:13 by Hibernate Tools 3.1.0.beta4

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.tas.dao.ITASAlias;

/**
 * Transfer Object that represents an entity of a Model (make´s model).
 * 
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name = "model")
public class Model implements ITransferObject, ILookupObject {

	/**
	 * Unique key
	 */
	private Integer id;

	/**
	 * Make of this model
	 */
	private Make make;

	/**
	 * Name of the model
	 */
	private String name;

	/**
	 * Void constructor
	 * 
	 */
	public Model() {
	}

	/**
	 * Constructor for this unique key.
	 * 
	 * @param id
	 *            unique key.
	 */
	public Model(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the unique key.
	 * 
	 * @return unique key.
	 */
	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}

	/**
	 * Assigns the unique key.
	 * 
	 * @param id
	 *            unique key.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Return the make of this model.
	 * 
	 * @return make.
	 * 
	 */
	@ManyToOne
	@JoinColumn(name = "make", nullable = false)
	public Make getMake() {
		return this.make;
	}

	/**
	 * Assigns a make to this model.
	 * 
	 * @param make
	 *            the make.
	 */
	public void setMake(Make make) {
		this.make = make;
	}

	/**
	 * Returns the name of this model.
	 * 
	 * @return name.
	 */
	@Column(length = 64, nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Assigns a name to this model.
	 * 
	 * @param name
	 *            the name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * toString
	 * 
	 * @return String
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append(getClass().getName()).append("@").append(
				Integer.toHexString(hashCode())).append(" [");
		buffer.append("id").append("='").append(getId()).append("' ");
		buffer.append("]");

		return buffer.toString();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.common.ILookupObject#getLookups()
	 */
	@Transient
    public Map<String,Object> getLookups() {
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(ITASAlias.MODEL_ID, getId());
        map.put(ITASAlias.MODEL_NAME, getName());
        map.put(ITASAlias.MODEL_MAKE_ID, getMake().getId());
        map.put(ITASAlias.MAKE_NAME, getMake().getName());
        return map;
    }

}
