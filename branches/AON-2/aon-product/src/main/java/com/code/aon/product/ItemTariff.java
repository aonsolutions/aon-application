package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object than represents the relationship between item and tariff.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name = "item_tariff")
public class ItemTariff implements ITransferObject {

	/**
	 * Unique key.
	 */
	private Integer id;

	/**
	 * Item linked.
	 */
	private Item item;

	/**
	 * Tariff linked.
	 */
	private Tariff tariff;

	/**
	 * Percentage to be applied.
	 */
	private double percentage;

	/**
	 * Void constructor.
	 */
	public ItemTariff() {
	}

	/**
	 * Constructor for this item and tariff.
	 * 
	 * @param pk
	 *            unique key.
	 * @param itemPK
	 *            Item of this relation.
	 * @param tariffPK
	 *            Tariff of the ralation.
	 */
	public ItemTariff(Integer pk, Item item, Tariff tariff) {
		this.id = pk;
		this.setItem(item);
		this.setTariff(tariff);
	}

	/**
	 * Return the unique key.
	 * 
	 * @return unique key.
	 */
	@Id
	@GeneratedValue
	@Column(nullable = false)
	public Integer getId() {
		return id;
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
	 * Returns the percentage to be applied.
	 * 
	 * @return percentage.
	 * @hibernate.property
	 */
	public double getPercentage() {
		return percentage;
	}

	/**
	 * Assigns the percentage to be applied.
	 * 
	 * @param percentage
	 *            the percentage.
	 */
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	/**
	 * Returns the item involved in this relationship.
	 * 
	 * @return item of the ralationship.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item", nullable = false)
	public Item getItem() {
		return item;
	}

	/**
	 * Assigns the item involved in this relationship.
	 * 
	 * @param item
	 *            item of the ralationship.
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Returns the tariff involved in this relationship.
	 * 
	 * @return tariff of the ralationship.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tariff", nullable = false)
	public Tariff getTariff() {
		return tariff;
	}

	/**
	 * Assigns the tariff involved in this relationship.
	 * 
	 * @param tariff
	 *            tariff of the ralationship.
	 */
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}
}