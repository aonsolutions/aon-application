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
import com.code.aon.product.enumeration.PluProductType;

/**
 * Transfer Object that represents the external codes 
 * linked to item.
 * 
 * @author Consulting & Development. Iñigo Gayarre - 27-jun-2006
 * @version 1.0
 * 
 */
@Entity
@Table(name = "item_pos")
public class ItemPos implements ITransferObject {

	/**
	 * Unique key.
	 */
	private Integer id;

	/**
	 * Item linked.
	 */
	private Item item;

    /**
     * PLU.
     */
    private String plu;

    /**
     * Barcode.
     */
    private String barcode;

    /**
     * Short description.
     */
    private String shortDescription;

    /**
     * Plu product type
     */
    private PluProductType pluProductType;
    
	/**
	 * Void constructor.
	 */
	public ItemPos() {
	}

	/**
	 * Returns the unique key.
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
	 * Returns the item linked.
	 * 
	 * @return item.
	 * 
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "item", nullable = false)
	public Item getItem() {
		return item;
	}

	/**
	 * Assigns the item to be linked.
	 * 
	 * @param item
	 *            item linked.
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Returns the barcode.
	 * 
	 * @return the barcode.
	 */
	@Column(name = "barcode")
	public String getBarcode() {
		return barcode;
	}

	/**
	 * Assigns the barcode.
	 * 
	 * @param barcode The barcode to set.
	 */
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	/**
	 * Returns the plu.
	 * 
	 * @return plu.
	 */
	@Column(name = "plu")
	public String getPlu() {
		return plu;
	}

	/**
	 * Assigns the plu.
	 * 
	 * @param plu The plu to set.
	 */
	public void setPlu(String plu) {
		this.plu = plu;
	}

	/**
	 * Returns the shortDescription
	 * 
	 * @return the shortDescription.
	 */
	@Column(name = "desc_short")
	public String getShortDescription() {
		return shortDescription;
	}

	/**
	 * Assigns the short description
	 * 
	 * @param shortDescription The short description to set.
	 */
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	/**
	 * @return the pluProductType
	 */
	@Column(name = "plu_product_type")
	public PluProductType getPluProductType() {
		return pluProductType;
	}

	/**
	 * @param pluProductType the pluProductType to set
	 */
	public void setPluProductType(PluProductType pluProductType) {
		this.pluProductType = pluProductType;
	}

	
}