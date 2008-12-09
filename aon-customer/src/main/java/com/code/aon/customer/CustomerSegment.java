package com.code.aon.customer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * The Class CustomerSegment.
 */
@Entity
@Table(name="customer_segment")
public class CustomerSegment implements ITransferObject {

	private static final long serialVersionUID = 7033076582842421725L;

	/** The id. */
	private Integer id;
	
	/** The description. */
	private String description;

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
	@Column(length=64, name="description")
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof CustomerSegment) {
			CustomerSegment cs = (CustomerSegment) obj;
			if (ObjectUtils.equals(getId(), cs.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}
