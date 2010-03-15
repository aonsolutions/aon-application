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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.sales.SalesDetail;
import com.code.aon.warehouse.enumeration.DeliveryDetailSource;
import com.code.aon.warehouse.enumeration.DeliveryDetailType;

@Entity
@Table(name="delivery_detail")
public class DeliveryDetail implements ITransferObject, ICalculable, IStockable {
	
	private static final long serialVersionUID = -5085790386141702008L;

	private Integer id;
	private Delivery delivery;
    private Integer line;
	private Item item;
	private String description;
	private Warehouse warehouse;
	private double quantity;
	private double price;
	private DiscountExpression discountExpression;
    private DeliveryDetailType type;
    private DeliveryDetailSource source;
	private SalesDetail salesDetail;
	
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
	@JoinColumn( name="delivery", nullable=false, updatable=false )
	public Delivery getDelivery() {
		return delivery;
	}
	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}
	
	public Integer getLine() {
		return line;
	}
	public void setLine(Integer line) {
		this.line = line;
	}

	@ManyToOne
	@JoinColumn( name="item", nullable=false )
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
	
	@ManyToOne
	@JoinColumn( name="warehouse", nullable = false )
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
	@Column(name ="discount_expr")
    @Type(type = "com.code.aon.product.util.DiscountExpressionUserType")
	public DiscountExpression getDiscountExpression() {
		return discountExpression;
	}
	public void setDiscountExpression(DiscountExpression discountExpression) {
		this.discountExpression = discountExpression;
	}

	public DeliveryDetailType getType() {
		return type;
	}
	public void setType(DeliveryDetailType type) {
		this.type = type;
	}

	public DeliveryDetailSource getSource() {
		return source;
	}
	public void setSource(DeliveryDetailSource source) {
		this.source = source;
	}

	@ManyToOne
	@JoinColumn( name="sales_detail" )
	public SalesDetail getSalesDetail() {
		return salesDetail;
	}
	public void setSalesDetail(SalesDetail salesDetail) {
		this.salesDetail = salesDetail;
	}

	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
	@Transient
	public boolean isEntry() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final DeliveryDetail o = (DeliveryDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.delivery, o.delivery)
				.append(this.line, o.line)
				.append(this.item, o.item)
				.append(this.description, o.description)
				.append(this.warehouse, o.warehouse)
				.append(this.quantity, o.quantity)
				.append(this.price, o.price)
				.append(this.discountExpression, o.discountExpression)
				.append(this.type, o.type)
				.append(this.source, o.source)
				.append(this.salesDetail, o.salesDetail)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(this.delivery)
			.append(this.line)
			.append(this.item)
			.append(this.description)
			.append(this.warehouse)
			.append(this.quantity)
			.append(this.price)
			.append(this.discountExpression)
			.append(this.type)
			.append(this.source)
			.append(this.salesDetail)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}