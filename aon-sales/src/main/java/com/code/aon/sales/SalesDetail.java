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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.enumeration.SalesDetailStatus;

@Entity
@Table(name="sales_detail")
public class SalesDetail implements ITransferObject, ICalculable {

	private static final long serialVersionUID = -5739011618986227394L;

    private Integer id;
    private Sales sales;
    private Integer line;
    private Item item;
    private String description;
    private double quantity;
    private double price;
    private DiscountExpression discountExpression;
    private double taxes;
    private SalesDetailStatus status;
    private OfferDetail offerDetail;
    private double delivered;
    private double transfered;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

	@ManyToOne
	@JoinColumn(name="sales", nullable=false , updatable=false)
    public Sales getSales() {
        return sales;
    }
    public void setSales(Sales sales) {
        this.sales = sales;
    }

    public Integer getLine() {
        return line;
    }
    public void setLine(Integer line) {
        this.line = line;
    }

	@ManyToOne
	@JoinColumn(name="item", nullable=false)
    public Item getItem() {
        return item;
    }
    public void setItem(Item item) {
        this.item = item;
    }

	@Column(length=1024)
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

	@Column(precision=15, scale=3)
    public double getQuantity() {
        return quantity;
    }
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

	@Column(precision=15, scale=4)
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = CommonUtil.round(price, 4);
    }

	@Column(name="discount_expr")
	@Type(type="com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

	@Column(precision=15, scale=3)
	public double getTaxes() {
		return taxes;
	}
	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}

	public SalesDetailStatus getStatus() {
		return status;
	}
	public void setStatus(SalesDetailStatus status) {
		this.status = status;
	}

	@ManyToOne
	@JoinColumn(name="offer_detail")
	public OfferDetail getOfferDetail() {
		return offerDetail;
	}
	public void setOfferDetail(OfferDetail offerDetail) {
		this.offerDetail = offerDetail;
	}

	@Column(precision=15, scale=3)
	public double getDelivered() {
		return delivered;
	}
	public void setDelivered(double delivered) {
		this.delivered = delivered;
	}

	@Transient
	public double getPendingQuantity() {
		return CommonUtil.round(quantity - delivered, 3);
	}
	@Transient
	public double getTransfered() {
		double pending = getPendingQuantity();
		transfered = (transfered > pending) ? pending : transfered;
		return transfered;
	}
	public void setTransfered(double transfered) {
		this.transfered = transfered;
	}

	@Transient
	public boolean isPending() {
		return getStatus() == SalesDetailStatus.PENDING;
	}
	@Transient
	public boolean isPartialSettled() {
		return getStatus() == SalesDetailStatus.PARTIAL_SETTLED;
	}
	@Transient
	public boolean isSettled() {
		return getStatus() == SalesDetailStatus.SETTLED;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final SalesDetail o = (SalesDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.delivered, o.delivered)
				.append(this.description, o.description)
				.append(this.discountExpression, o.discountExpression)
				.append(this.item, o.item)
				.append(this.line, o.line)			
				.append(this.offerDetail, o.offerDetail)
				.append(this.price, o.price)
				.append(this.quantity, o.quantity)
				.append(this.sales, o.sales)
				.append(this.status, o.status)
				.append(this.taxes, o.taxes)
				.append(this.transfered, o.transfered)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(delivered)
			.append(description)
			.append(discountExpression)
			.append(id)	
			.append(item)			
			.append(line)			
			.append(offerDetail)			
			.append(price)
			.append(quantity)
			.append(sales)
			.append(status)
			.append(taxes)
			.append(transfered)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}