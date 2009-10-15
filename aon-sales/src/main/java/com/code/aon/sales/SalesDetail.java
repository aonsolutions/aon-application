package com.code.aon.sales;

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

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.enumeration.SalesDetailSource;
import com.code.aon.sales.enumeration.SalesDetailStatus;

/**
 * Transfer Object that represents a line of a sale.
 */
@Entity
@Table(name="sales_detail")
public class SalesDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -5739011618986227394L;

	/** The id. */
    private Integer id;

    /** The sale. */
    private Sales sales;

    /** The line. */
    private Integer line;

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
    
    /** The taxes. */
    private double taxes;

    /** The status. */
    private SalesDetailStatus status;

    /** The source. */
    private SalesDetailSource source;

    /** The offer detail. */
    private OfferDetail offerDetail;

    /** The delivered. */
    private double delivered;

    /** The transfered. */
    private double transfered;

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
	 * Gets the sales.
	 * 
	 * @return the sales
	 */
	@ManyToOne
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
	public SalesDetailStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the status 
	 */
	public void setStatus(SalesDetailStatus status) {
		this.status = status;
	}

	/**
     * Gets the source.
     * 
     * @return the source
     */
	public SalesDetailSource getSource() {
		return source;
	}

	/**
	 * Sets the source.
	 * 
	 * @param source the source
	 */
	public void setSource(SalesDetailSource source) {
		this.source = source;
	}

	/**
     * Gets the offer detail.
     * 
     * @return the offer detail
     */
	@ManyToOne
	@JoinColumn( name="offer_detail" )
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}

	/**
	 * Sets the offer detail.
	 * 
	 * @param offerDetail the offer detail
	 */
	public void setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
	}

	/**
	 * Gets the delivered.
	 * 
	 * @return the delivered
	 */
	public double getDelivered() {
		return delivered;
	}

	/**
	 * Sets the delivered.
	 * 
	 * @param delivered the delivered
	 */
	public void setDelivered(double delivered) {
		this.delivered = delivered;
	}

	@Transient
	public double getTransfered() {
		transfered = transfered > (quantity - delivered) ? (quantity - delivered) : transfered;
		return transfered;
	}

	public void setTransfered(double transfered) {
		this.transfered = transfered;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof SalesDetail) {
			SalesDetail s = (SalesDetail) obj;
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