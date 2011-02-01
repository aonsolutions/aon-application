package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.ProductCategory;

@Entity
@Table(name="commission_category")
public class CommissionCategory implements ITransferObject {

	private static final long serialVersionUID = 1757116118950029779L;

	private Integer id;
	private Commission commission;
	private ProductCategory category;
	private double quantity;
	private double rate;

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="commission", nullable=false)
	@ForeignKey(name = "FK_COMMISSION_CATEGORY_COMMISSION")
	@Index(name = "IDX_COMMISSION_CATEGORY_COMMISSION")
	public Commission getCommission() {
		return commission;
	}

	public void setCommission(Commission commission) {
		this.commission = commission;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="category", nullable=false)
	@ForeignKey(name = "FK_COMMISSION_CATEGORY_CATEGORY")
	@Index(name = "IDX_COMMISSION_CATEGORY_CATEGORY")	
	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	@Column(precision = 6, scale = 2)
	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CommissionCategory o = (CommissionCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.commission, o.commission)
				.append(this.category, o.category)				
				.append(this.quantity, o.quantity)								
				.append(this.rate, o.rate)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(commission)
			.append(category)
			.append(quantity)						
			.append(rate)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
