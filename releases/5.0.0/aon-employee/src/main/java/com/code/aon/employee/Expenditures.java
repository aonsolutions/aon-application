/**
 * 
 */
package com.code.aon.employee;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 28/08/2007
 *
 */
@Entity
@Table(name="expenditures")
public class Expenditures implements ITransferObject {

	private static final long serialVersionUID = 4435185915356406070L;

	/** Expenditures identifier. */
	private Integer id;
	private Integer resource;
	private ExpendituresItems item;
	private Date date;
	private Double amount;
	

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the resource
	 */
    public Integer getResource() {
		return resource;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(Integer resource) {
		this.resource = resource;
	}

	/**
	 * @return the item
	 */
	@OneToOne
    @JoinColumn(name="expenditures_item", nullable = false)
	public ExpendituresItems getItem() {
		return item;
	}

	/**
	 * @param item the type to set
	 */
	public void setItem(ExpendituresItems item) {
		this.item = item;
	}

	/**
	 * @return date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
