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
import com.code.aon.sales.SalesDetail;

/**
 * Transfer Object that represents a Delivery Detail line.
 * 
 * @author igayarre
 *
 */
@Entity
@Table(name="delivery_detail")
public class DeliveryDetail implements ITransferObject, ICalculable, IStockable{
	
	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * The Delivery that contains the line
	 */
	private Delivery delivery;
	
    /**
     * The line number
     */
    private int line;
	
	/**
	 * The Item referenced in the line
	 */
	private Item item;
	
	/**
	 * The description of the line
	 */
	private String description;
	
	/**
	 * Warehouse linked
	 */
	private Warehouse warehouse;
	
	/**
	 * Quantity of Items
	 */
	private double quantity;
	
	/**
	 * The price
	 */
	private double price;
	
	/**
	 * Discount to apply
	 */
	private DiscountExpression discountExpression;
	
	/**
	 * The Sales Detail line linked
	 */
	private SalesDetail salesDetail;
	
	
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
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	/**
	 * Returns the Delivery
	 * 
	 * @return Returns the delivery.
	 * 
	 */
	@ManyToOne
	@JoinColumn( name="delivery",nullable=false )
	public Delivery getDelivery() {
		return delivery;
	}
	/**
	 * Assigns the Delivery
	 * 
	 * @param delivery The delivery to set.
	 */
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}
	
	/**
	 * Returns the line number
	 * 
	 * @return Returns the line.
	 * 
	 */
	@Column(nullable=false)	
	public int getLine() {
		return line;
	}
	/**
	 * Assigns the line number
	 * 
	 * @param line The line to set.
	 */
	public void setLine(int line) {
		this.line = line;
	}
	/**
	 * Returns the Item 
	 * 
	 * @return Returns the item.
	 */
	@ManyToOne
	@JoinColumn( name="item",nullable=false )
	public Item getItem() {
		return item;
	}
	/**
	 * Assgins a Item to this detail
	 * 
	 * @param item The item to set.
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	/**
	 * Returns the description of this line
	 * 
	 * @return Returns the description.
	 * 
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * Assigns the description
	 * 
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the quantity
	 * 
	 * @return Returns the quantity.
	 */
	@Column
	public double getQuantity() {
		return quantity;
	}
	/**
	 * Assigns the quantity of Items
	 * 
	 * @param quantity The quantity to set.
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Returns the price
	 * 
	 * @return Returns the price.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Assigns the price of the line
	 * 
	 * @param price The price to set.
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
	 * Returns the SalesDetail linked by this line
	 * 
	 * @return Returns the salesDetail.
	 */
	@ManyToOne
	@JoinColumn( name="sales_detail" )
	public SalesDetail getSalesDetail() {
		return salesDetail;
	}
	/**
	 * Assigns the SalesDetail 
	 * 
	 * @param salesDetail The salesDetail to set.
	 */
	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}
	/**
	 * Returns the warehouse
	 * 
	 * @return the warehouse.
	 */
	@ManyToOne
	@JoinColumn( name="warehouse", updatable = false )
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
		return false;
	}

	/**
	 * Returns the total amount once applied the price, quantity and discounts. 
	 * 
	 * @return amount
	 */
	@Transient
	public double getAmount() {
		double total = getPrice() * getQuantity();
		if(getDiscountExpression() != null){
			double discounts[] = getDiscountExpression().getDiscounts();
			for(int i = 0;i<discounts.length;i++){
				total = round(total - (discounts[i] * total / 100), 2);
			}
		}
		return total;
	}
	
	/**
	 * Rounds the double value
	 * 
	 * @param value to round
	 * @param precision of decimals
	 * @return value rounded
	 */
	@Transient
	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }

	/**
	 * @return taxes. Taxes applied to the deliveryDetail
	 */
	//TODO implementar método
	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
}