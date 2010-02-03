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
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.warehouse.enumeration.IncomeDetailSource;
import com.code.aon.warehouse.enumeration.IncomeDetailType;

/**
 * Transfer Object that represents a Income Detail line.
 */
@Entity
@Table(name="income_detail")
public class IncomeDetail implements ITransferObject, ICalculable, IStockable {
	
	private static final long serialVersionUID = 3100497435533821492L;

	/** The id. */
	private Integer id;
	
	/** The income. */
	private Income income;
	
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
    private IncomeDetailType type;

    /** The source. */
    private IncomeDetailSource source;

    /** The purchase detail. */
	private PurchaseDetail purchaseDetail;
	
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
	 * Gets the income.
	 * 
	 * @return the income
	 */
	@ManyToOne
    @JoinColumn( name="income", nullable = false, updatable = false )
	public Income getIncome() {
		return income;
	}

    /**
     * Sets the income.
     * 
     * @param income the income
     */
	public void setIncome(Income income) {
		this.income = income;
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
	public IncomeDetailType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type
	 */
	public void setType(IncomeDetailType type) {
		this.type = type;
	}

	/**
     * Gets the source.
     * 
     * @return the source
     */
	public IncomeDetailSource getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 * 
	 * @param source the source
	 */
	public void setSource(IncomeDetailSource source) {
		this.source = source;
	}

	/**
     * Gets the purchase detail.
     * 
     * @return the purchase detail
     */
	@ManyToOne
    @JoinColumn( name="purchase_detail" )
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	/**
	 * Sets the purchase detail.
	 * 
	 * @param purchaseDetail the purchase detail
	 */
	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
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
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof IncomeDetail) {
			IncomeDetail s = (IncomeDetail) obj;
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