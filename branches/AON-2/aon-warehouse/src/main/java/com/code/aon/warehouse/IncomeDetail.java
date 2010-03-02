package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.purchase.PurchaseDetail;

/**
 * Transfer Object that represents a Income Detail line.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
@Entity
@Table(name="income_detail")
public class IncomeDetail implements ITransferObject, ICalculable, IStockable {
	
	/**
	 * Unique Key
	 */
	private Integer id;
	
	/**
	 * The Income header
	 */
	private Income income;
	
	/**
	 * The Item linked in this income detail
	 */
	private Item item;
	
	/**
	 * Description 
	 */
	private String description;
	
	/**
	 * Warehouse linked
	 */
	private Warehouse warehouse;
	
	/**
	 * Item quantity
	 */
	private double quantity;
	
	/**
	 * Item price
	 */
	private double price;
	
	/**
	 * Discount to be applied
	 */
	private DiscountExpression discountExpression;
	
	/**
	 * Purchase detail line linked
	 */
	private PurchaseDetail purchaseDetail;
	
	/**
	 * Returns the unique key
	 * 
	 * @return unique key
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
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
	 * Returns the Income 
	 * 
	 * @return income
	 */
	@ManyToOne
    @JoinColumn(name="income", nullable = false, updatable = false)
	public Income getIncome() {
		return income;
	}

	/**
	 * Assigns the income
	 * 
	 * @param income
	 */
	public void setIncome(Income income) {
		this.income = income;
	}

	/**
	 * Returns the Item linked
	 * 
	 *@return item
	 * @see com.code.aon.product.strategy.ICalculable#getItem()
	 */
	@ManyToOne
    @JoinColumn(name="item", updatable = false)
	public Item getItem() {
		return item;
	}

	/**
	 * Assigns the Item
	 * 
	 * @param item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Returns the description
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Assgins a description
	 * 
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the purchase detail linked to this Income Detail
	 * 
	 * @return purchase detail
	 */
	@ManyToOne
    @JoinColumn(name="purchase_detail", updatable = false)
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	/**
	 * Assigns a purchase detail to be linked to this Income Detail
	 * 
	 * @param purchaseDetail
	 */
	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	/**
	 * Returns the Item quantity
	 * 
	 * @return quantity of items
	 * @see com.code.aon.product.strategy.ICalculable#getQuantity()
	 */
	@Column(name="quantity")
	public double getQuantity() {
		return quantity;
	}

	/**
	 * Assigns the item quantity
	 * 
	 * @param quantity
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Returns the price of the Item in this Income
	 * 
	 * @return price
	 * @see com.code.aon.product.strategy.ICalculable#getPrice()
	 */
	@Column(name="price")
	public double getPrice() {
		return price;
	}

	/**
	 * Assigns the price
	 * 
	 * @param price
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * Return the discount expression
	 * 
	 * @return discount expression applied
	 * @see com.code.aon.product.strategy.ICalculable#getDiscountExpression()
	 */
	@Column(name ="discount_expr")
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
	public DiscountExpression getDiscountExpression() {
		return discountExpression;
	}

	/**
	 * Assigns the discount expression
     * 
     * @param discountExpression
     */
	public void setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
	}

	/**
	 * Returns the warehouse
	 * 
	 * @return the warehouse.
	 */
	@ManyToOne
    @JoinColumn(name="warehouse", updatable = false)
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Assigns the warehouse to set.
	 * 
	 * @param warehouse The warehouse to set.
	 */
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.warehouse.IStockable#isEntry()
	 */
	@Transient
	public boolean isEntry() {
		return true;
	}
	
	/**
	 * @return taxes. Taxes applied to the incomeDetail
	 */
	@Transient
	@SuppressWarnings("unused")
	public double getTaxes() throws ManagerBeanException{
		return (getPurchaseDetail() != null) ? getPurchaseDetail().getTaxes() : 0;
	}
}