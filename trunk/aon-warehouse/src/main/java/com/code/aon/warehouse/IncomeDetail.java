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
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.warehouse.enumeration.IncomeDetailSource;
import com.code.aon.warehouse.enumeration.IncomeDetailType;

@Entity
@Table(name="income_detail")
public class IncomeDetail implements ITransferObject, ICalculable, IStockable {
	
	private static final long serialVersionUID = 3100497435533821492L;

	private Integer id;
	private Income income;
    private Integer line;
	private Item item;
	private String description;
	private Warehouse warehouse;
	private double quantity;
	private double price;
	private DiscountExpression discountExpression;
    private IncomeDetailType type;
    private IncomeDetailSource source;
	private PurchaseDetail purchaseDetail;
	
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
    @JoinColumn( name="income", nullable = false, updatable = false )
	public Income getIncome() {
		return income;
	}
	public void setIncome(Income income) {
		this.income = income;
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

	public IncomeDetailType getType() {
		return type;
	}
	public void setType(IncomeDetailType type) {
		this.type = type;
	}

	public IncomeDetailSource getSource() {
		return source;
	}
	public void setSource(IncomeDetailSource source) {
		this.source = source;
	}

	@ManyToOne
    @JoinColumn( name="purchase_detail" )
	public PurchaseDetail getPurchaseDetail() {
		return purchaseDetail;
	}

	public void setPurchaseDetail(PurchaseDetail purchaseDetail) {
		this.purchaseDetail = purchaseDetail;
	}

	@Transient
	public double getTaxes() throws ManagerBeanException {
		return 0;
	}
	
	@Transient
	public boolean isEntry() {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final IncomeDetail o = (IncomeDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.income, o.income)
				.append(this.line, o.line)
				.append(this.item, o.item)
				.append(this.description, o.description)
				.append(this.warehouse, o.warehouse)
				.append(this.quantity, o.quantity)
				.append(this.price, o.price)
				.append(this.discountExpression, o.discountExpression)
				.append(this.type, o.type)
				.append(this.source, o.source)
				.append(this.purchaseDetail, o.purchaseDetail)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(this.income)
			.append(this.line)
			.append(this.item)
			.append(this.description)
			.append(this.warehouse)
			.append(this.quantity)
			.append(this.price)
			.append(this.discountExpression)
			.append(this.type)
			.append(this.source)
			.append(this.purchaseDetail)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
}