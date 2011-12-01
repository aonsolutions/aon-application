package com.code.aon.commercial;

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

import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;

@Entity
@Table(name="offer_detail")
public class OfferDetail implements ITransferObject, ICalculable {
	
	private static final long serialVersionUID = -3195621161952952987L;

	private Integer id;
	private Offer offer;
	private Integer line;
	private Item item;
    private String description;
    private double quantity;
    private double price;
    private DiscountExpression discountExpression;
    private OfferDetailStatus status;

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
	@JoinColumn(name="offer", nullable=false , updatable=false)
	public Offer getOffer() {
		return offer;
	}
	public void setOffer(Offer offer) {
		this.offer = offer;
	}

    public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}

	@ManyToOne
	@JoinColumn(name="item")
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

	@Column(precision=15, scale=3)
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

	public OfferDetailStatus getStatus() {
		return status;
	}
	public void setStatus(OfferDetailStatus status) {
		this.status = status;
	}

	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final OfferDetail o = (OfferDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description, o.description)				
				.append(this.discountExpression, o.discountExpression)
				.append(this.item, o.item)				
				.append(this.line, o.line)
				.append(this.offer, o.offer)				
				.append(this.price, o.price)
				.append(this.quantity, o.quantity)				
				.append(this.status, o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(description)	
			.append(discountExpression)			
			.append(id)			
			.append(item)
			.append(line)
			.append(offer)
			.append(price)
			.append(quantity)
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}