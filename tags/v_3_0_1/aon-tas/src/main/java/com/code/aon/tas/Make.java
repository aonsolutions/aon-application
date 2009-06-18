package com.code.aon.tas;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ILookupObject;
import com.code.aon.common.ITransferObject;
import com.code.aon.tas.dao.ITASAlias;

/**
 * Transfer Object that represents a Make.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name = "make")
public class Make implements ITransferObject, java.io.Serializable, ILookupObject {

	/**
	 * Unique key
	 */
	private Integer id;

	/**
	 * Name of the make
	 */
	private String name;

	/**
	 * Void constructor
	 */
	public Make() {
	}

	/**
	 * Contructor with key parameter
	 * 
	 * @param id unique key
	 */
	public Make(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the unique key
	 * 
	 * @return unique key
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return this.id;
	}

	/**
	 * Assigns the unique key
	 * 
	 * @param primaryKey
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Returns the name of the make
	 * 
	 * @return name
	 */
	@Column(length = 64, nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Assigns the name of the amke
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
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
        map.put(ITASAlias.MAKE_ID, getId());
        map.put(ITASAlias.MAKE_NAME, getName());
        return map;
    }

}
