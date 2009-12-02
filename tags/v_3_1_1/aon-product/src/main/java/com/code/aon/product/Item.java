package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.annotations.AonPOJOInitializationInvalidateRestoreNull;
import com.code.aon.product.enumeration.ProductStatus;

/**
 * Transfer Object that represents an item.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 *  
 */
@Entity
@Table(name="item")
public class Item implements ITransferObject {

	private static final long serialVersionUID = -2720748805321005422L;
	
    /**
     * Unique key.
     */
    private Integer id;

    /**
     * Product that references this item.
     */
    private Product product;

    /**
     * Item detail.
     */
    private String detail;

    /**
     * Item description.
     */
    private String description;

    /**
     * Item price.
     */
    private double price;

    /**
     * Item status.
     */
    private ProductStatus status;

    /**
     * Expenses percent.
     */
    private double expensesPercent;

    /**
     * Sure expenses.
     */
    private double expensesFixed;
    
    /**
     * Sales's profit percent.
     */
    private double profitPercent;
    
    /**
	 * Last purchase price.
	 */
    private double purchasePrice;

    /**
     * Default contructor.
     * 
     */
    public Item() {
    }

    /**
     * Constructor for this unique key and product.
     * 
     * @param pk
     *            Unique key.
     * @param product
     *            Product that references this item.
     */
    public Item(Integer pk, Product product) {
        this.id = pk;
        this.setProduct(product);
    }

    /**
     * Returns the unique key.
     * 
     * @return Unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Asigns the unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns article's detail.
     * 
     * @return article's detail.
     */
    @Column(length=64)
    public String getDetail() {
        return detail;
    }

    /**
     * Asigns article's detail.
     * 
     * @param detail
     *            article's detail.
     */
    public void setDetail(String detail) {
        this.detail = detail;
    }

    /**
     * Returns this item's description.
     * 
     * @return item's description.
     */
    @Column(length=65535)
    public String getDescription() {
        return description;
    }

    /**
     * Asign this item's description.
     * 
     * @param description
     *            item's description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns item's price.
     * 
     * @return item's price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Asigns item's price.
     * 
     * @param price
     *            item's price.
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns item's product.
     * 
     * @return item's product.
     * 
     */
    @ManyToOne
    @JoinColumn(name="product", nullable=false)
    @org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
    @AonPOJOInitializationInvalidateRestoreNull
    public Product getProduct() {
        return product;
    }

    /**
     * Asigns this item's product.
     * 
     * @param product
     *            item's product.
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Returns item's status.
     * 
     * @return item's status.
     */
    public ProductStatus getStatus() {
        return status;
    }

	/**
     * Asigns item's status.
     * 
     * @param status
     *            item's status.
     */
    public void setStatus(ProductStatus status) {
        this.status = status;
    }
    
	/**
	 * Returns sure expenses 
	 * 
	 * @return Returns sure expenses.
	 */
    @Column(name="expenses_fixed")
	public double getExpensesFixed() {
		return expensesFixed;
	}

	/**
	 * Asigns sure expenses
	 * 
	 * @param expensesFixed sure expenses.
	 */
	public void setExpensesFixed(double expensesFixed) {
		this.expensesFixed = expensesFixed;
	}

	/**
	 * Returns the expenses percent
	 * 
	 * @return Returns expenses percent.
	 */
    @Column(name="expenses_percent")
	public double getExpensesPercent() {
		return expensesPercent;
	}

	/**
	 * Asigns the expenses percent
	 * 
	 * @param expensesPercent expenses percent.
	 */
	public void setExpensesPercent(double expensesPercent) {
		this.expensesPercent = expensesPercent;
	}

	/**
	 * Returns the profit percent
	 * 
	 * @return Returns profit percent.
	 */
	@Column(name="profit_percent")
	public double getProfitPercent() {
		return profitPercent;
	}

	/**
	 * Asigns the profit percent
	 * 
	 * @param profitPercent profit percent.
	 */
	public void setProfitPercent(double profitPercent) {
		this.profitPercent = profitPercent;
	}

	/**
	 * Returns the purchase price
	 * 
	 * @return Returns purchase price.
	 */
	@Column(name="purchase_price")
	public double getPurchasePrice() {
		return purchasePrice;
	}
	
	/**
	 * Asigns the purchase price
	 * 
	 * @param purchasePrice purchase price.
	 */
    public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Item) {
			Item item = (Item) obj;
			if (ObjectUtils.equals(getId(), item.getId())) {
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