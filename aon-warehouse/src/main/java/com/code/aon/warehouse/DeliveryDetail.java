package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.SalesDetail;
import com.code.aon.warehouse.enumeration.DeliveryDetailSource;
import com.code.aon.warehouse.enumeration.DeliveryDetailType;

/**
 * Transfer Object that represents a Delivery Detail line.
 */
@Entity
@Table(name="delivery_detail")
public class DeliveryDetail implements ITransferObject, ICalculable, IStockable {
	
	private static final long serialVersionUID = -5085790386141702008L;

	/** The id. */
	private Integer id;

	/** The delivery. */
	private Delivery delivery;
	
	/** The line. */
    private Integer line;
	
	/** The item. */
	private Item item;
	
	/** The description. */
	private String description;
	
	/** The warehouse. */
	private Warehouse warehouse;
	
	/** The quantity. */
	private double quantity;
	
	/** The price. */
	private double price;
	
	/** The discount expression. */
	private DiscountExpression discountExpression;
	
    /** The type. */
    private DeliveryDetailType type;

    /** The source. */
    private DeliveryDetailSource source;

    /** The sales detail. */
	private SalesDetail salesDetail;
	
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
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	/**
	 * Gets the delivery.
	 * 
	 * @return the delivery
	 */
	@ManyToOne
	@JoinColumn( name="delivery", nullable=false, updatable=false )
	public Delivery getDelivery() {
		return delivery;
	}

    /**
     * Sets the delivery.
     * 
     * @param delivery the delivery
     */
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}
	
    /**
     * Gets the line.
     * 
     * @return the line
     */
	public Integer getLine() {
		return line;
	}

    /**
     * Sets the line.
     * 
     * @param line the line
     */
	public void setLine(Integer line) {
		this.line = line;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne
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
	@Column(length=1024)
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
	 * Gets the warehouse
	 * 
	 * @return the warehouse.
	 */
	@ManyToOne
	@JoinColumn( name="warehouse", nullable = false )
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Sets the warehouse.
	 * 
	 * @param warehouse the warehouse
	 */
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
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

	/**
     * Gets the type.
     * 
     * @return the type
     */
	public DeliveryDetailType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(DeliveryDetailType type) {
		this.type = type;
	}

	/**
     * Gets the source.
     * 
     * @return the source
     */
	public DeliveryDetailSource getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 * 
	 * @param source the source
	 */
	public void setSource(DeliveryDetailSource source) {
		this.source = source;
	}

	/**
     * Gets the sales detail.
     * 
     * @return the sales detail
     */
	@ManyToOne
	@JoinColumn( name="sales_detail" )
	public SalesDetail getSalesDetail() {
		return salesDetail;
	}

	/**
	 * Sets the offer detail.
	 * 
	 * @param offerDetail the offer detail
	 */
	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}

	/**
	 * Gets the taxes. Necessary to implement <code>ICalculable</code>
	 * 
	 * @return the taxes
	 */
	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}

	/**
	 * Gets the taxes. Necessary to implement <code>IStockable</code>
	 * 
	 * @return the entry
	 */
	@Transient
	public boolean isEntry() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof DeliveryDetail) {
			DeliveryDetail s = (DeliveryDetail) obj;
			if (s.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), s.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}