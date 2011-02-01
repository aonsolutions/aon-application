package com.code.aon.product;

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

@Entity
@Table(name="catalogue_category")
public class CatalogueCategory implements ITransferObject {

	private static final long serialVersionUID = -1595222928098471123L;

	private Integer id;
	private Catalogue catalogue;
	private ProductCategory category;
	private double quantity;
	private double discount;

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
	@JoinColumn(name="catalogue", nullable=false)
	@ForeignKey(name = "FK_CATALOGUE_CATEGORY_CATALOGUE")
	@Index(name = "IDX_CATALOGUE_CATEGORY_CATALOGUE")
	public Catalogue getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="category", nullable=false)
	@ForeignKey(name = "FK_CATALOGUE_CATEGORY_CATEGORY")
	@Index(name = "IDX_CATALOGUE_CATEGORY_CATEGORY")	
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
	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CatalogueCategory o = (CatalogueCategory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.catalogue, o.catalogue)
				.append(this.category, o.category)				
				.append(this.quantity, o.quantity)								
				.append(this.discount, o.discount)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(catalogue)
			.append(category)
			.append(quantity)						
			.append(discount)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
