package com.code.aon.sales;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.enumeration.SalesDetailStatus;

/**
 * Transfer Object that represents a line of a sale.
 * 
 * @author jurkiri
 */
@Entity
@Table(name="sales_detail")
public class SalesDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -5739011618986227394L;

	/** The id. */
    private Integer id;

    /** The sale. */
    private Sales sales;

    /** The number of the line. */
    private int line;

    /** The item. */
    private Item item;

    /** The description. */
    private String description;

    /** The quantity. */
    private double quantity;

    /** The price. */
    private double price;
    
    /** The discount expression to be applied. */
    private DiscountExpression discountExpression;
    
    /** The taxes to be applied to this SalesDetail. */
    private double taxes;

    /** The status of the salesDetail. */
    private SalesDetailStatus salesDetailStatus;

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
	 * Gets the sale.
	 * 
	 * @return the sale
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="sales", nullable=false , updatable=false)
    public Sales getSales() {
        return sales;
    }

    /**
     * Sets the sales.
     * 
     * @param sales the sales
     */
    public void setSales(Sales sales) {
        this.sales = sales;
    }

    /**
     * Gets the line.
     * 
     * @return the line
     */
    @Column (nullable=false)
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
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="item", nullable=false , updatable=false)
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
     * Gets the status of the SalesDetail.
     * 
     * @return the status of the SalesDetail
     */
    @Column(name="status")
	public SalesDetailStatus getSalesDetailStatus() {
		return salesDetailStatus;
	}

	/**
	 * Sets the status of the SalesDetail.
	 * 
	 * @param salesDetailStatus the status of the SalesDetail
	 */
	public void setSalesDetailStatus(SalesDetailStatus salesDetailStatus) {
		this.salesDetailStatus = salesDetailStatus;
	}

	/**
	 * Gets the taxes to be applied.
	 * 
	 * @return the taxes
	 */
	public double getTaxes() {
		return taxes;
	}

	/**
	 * Sets the taxes to be applied.
	 * 
	 * @param taxes the taxes
	 */
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}
}