package com.code.aon.product;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a Catalogue.
 * 
 * @author Consulting & Development. Gorka Irazu - 18/07/2008
 */
@Entity
@Table(name="catalogue")
public class Catalogue implements ITransferObject {

	private static final long serialVersionUID = -4171388932127976688L;

	/** The Id. */
	private Integer Id;
	
	/** The name. */
	private String name;
	
	/** The start date. */
	private Date startDate;
	
	/** The end date. */
	private Date endDate;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return Id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		Id = id;
	}
	
	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	@Column(length=32)
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
	 * Gets the start date.
	 * 
	 * @return the start date
	 */
	@Column(name="start_date")
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Gets the end date.
	 * 
	 * @return the end date
	 */
	@Column(name="end_date")
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Catalogue) {
			Catalogue catalogue = (Catalogue) obj;
			if (ObjectUtils.equals(getId(), catalogue.getId())) {
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
