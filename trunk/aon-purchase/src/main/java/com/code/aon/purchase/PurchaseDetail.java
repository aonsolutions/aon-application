package com.code.aon.purchase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;

/**
 * Transfer Object that represents a line of a purchase.
 * 
 * @author Joseba Urkiri
 */
@Entity
@Table(name="purchase_detail")
public class PurchaseDetail implements ITransferObject {

	/** The id. */
	private Integer id;
	
	/** The purchase which contains this line. */
	private Purchase purchase;
	
	/** The line number. */
	private int line;
	
	/** The item. */
	private Item item;

    /** The description. */
    private String description;
	
	/** The quantity. */
	private double quantity;
	
	/** The price. */
	private double price;
	
	/** The taxes (canon). */
	private double taxes;
	
	/** The status. */
	private PurchaseDetailStatus status;
	
	/** The discount expression. */
    private DiscountExpression discountExpression;

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
	 * Gets the purchase.
	 * 
	 * @return the purchase
	 */
	@ManyToOne
	@JoinColumn( name="purchase", nullable = false, updatable=false)
	public Purchase getPurchase() {
		return purchase;
	}

	/**
	 * Sets the purchase.
	 * 
	 * @param purchase the purchase
	 */
	public void setPurchase(Purchase purchase) {
		this.purchase = purchase;
	}
	
	/**
	 * Gets the line.
	 * 
	 * @return the line
	 */
	@Column(nullable=false)
	public int getLine() {
		return line;
	}

	/**
	 * Sets the line.
	 * 
	 * @param line the line
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne
	@JoinColumn( name="item", nullable = false, updatable=false)
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
    @Column(name="description")
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
    @Column(name="quantity")
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
	@Column(name="price")
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
	 * Gets the taxes.
	 * 
	 * @return the taxes
	 */
	public double getTaxes() {
		return taxes;
	}

	/**
	 * Sets the taxes.
	 * 
	 * @param taxes the taxes
	 */
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Column(name="status")
	public PurchaseDetailStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status
	 */
	public void setStatus(PurchaseDetailStatus status) {
		this.status = status;
	}
	
	/**
     * Gets the discount expression.
     * 
     * @return the discount expression
     */
    @Column(name ="discount_expr")
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
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
}
