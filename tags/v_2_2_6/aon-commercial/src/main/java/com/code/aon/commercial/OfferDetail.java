package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;

/**
 * Transfer Object that represents a OfferDetail.
 */
@Entity
@Table(name="offer_detail")
public class OfferDetail implements ITransferObject,ICalculable {
	
	/** The Id. */
	private Integer Id;
	
	/** The offer. */
	private Offer offer;
	
	/** The item. */
	private Item item;

    /** The description. */
    private String description;

    /** The quantity. */
    private double quantity;

    /** The price. */
    private double price;
    
    /** The discount expression. */
    private DiscountExpression discountExpression;
    
    /** The status. */
    private OfferDetailStatus status;

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
	 * Gets the offer.
	 * 
	 * @return the offer
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="offer", nullable=false , updatable=false)
	public Offer getOffer() {
		return offer;
	}

	/**
	 * Sets the offer.
	 * 
	 * @param offer the offer
	 */
	public void setOffer(Offer offer) {
		this.offer = offer;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="item", nullable=false )
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * 
	 * @param item the item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

    /**
     * Gets the description.
     * 
     * @return the description
     */
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

    /**
     * Gets the quantity.
     * 
     * @return the quantity
     */
    @Column(nullable=false)
    public double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     * 
     * @param quantity the quantity
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

	/**
	 * Gets the price.
	 * 
	 * @return the price
	 */
    @Column(nullable=false)
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price.
	 * 
	 * @param price the price
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Gets the discount expression.
	 * 
	 * @return the discount expression
	 */
	@Column(name="discount_expr")
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
	public DiscountExpression getDiscountExpression() {
		return discountExpression;
	}

	/**
	 * Sets the discount expression.
	 * 
	 * @param discountExpression the discount expression
	 */
	public void setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public OfferDetailStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(OfferDetailStatus status) {
		this.status = status;
	}

	/**
	 * Gets the taxes.
	 * 
	 * @return the taxes
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
}