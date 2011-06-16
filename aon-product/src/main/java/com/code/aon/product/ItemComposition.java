package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;

@Entity
@Table(name="item_composition")
public class ItemComposition implements ITransferObject{
	
	private static final long serialVersionUID = -6415420632921077499L;

	private Integer id;
	private Item item;
	private Item compositionItem;
    private int sequence;
    private String description;
    private double quantity;
    private double price;
    private DiscountExpression discountExpression;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	@ManyToOne
	@JoinColumn(name="item",nullable=false)
    @ForeignKey(name = "FK_ITEM_COMPOSITION_ITEM")
    @Index(name = "IDX_ITEM_COMPOSITION_ITEM")
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
    @ManyToOne
    @JoinColumn(name="composition_item")
    @ForeignKey(name = "FK_ITEM_COMPOSITION_COMPOSITION")
    @Index(name = "IDX_ITEM_COMPOSITION_COMPOSITION")
    public Item getCompositionItem() {
        return compositionItem;
    }
    public void setCompositionItem(Item compositionItem) {
        this.compositionItem = compositionItem;
    }

    public int getSequence() {
        return sequence;
    }
    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    @Column(name ="discount_expr",length=32)
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
    public DiscountExpression getDiscountExpression() {
        return discountExpression;
    }
    public void setDiscountExpression(DiscountExpression discountExpression) {
        this.discountExpression = discountExpression;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemComposition o = (ItemComposition) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.compositionItem, o.compositionItem)
				.append(this.description, o.description)
				.append(this.discountExpression, o.discountExpression)
				.append(this.item, o.item)
				.append(this.price, o.price)
				.append(this.quantity, o.quantity)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(this.compositionItem)
			.append(this.description)
			.append(this.discountExpression)
			.append(this.item)			
			.append(this.price)
			.append(this.quantity)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}