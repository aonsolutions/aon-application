package com.code.aon.cms;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.cms.util.IActivableObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents a product.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name="product")
public class Product implements IActivableObject {

	private static final long serialVersionUID = 9207032363854912347L;

	/**
     * Unique key.
     */
    private Integer id;

    /**
     * Product's name.
     */
    private String alias;

    /**
     * Active
     */
	private boolean active = true;
	
    /**
     * Price
     */
	private BigDecimal price;
	
    /**
     * Offer Price
     */
	private BigDecimal offerPrice;
	
    /**
     * Brand
     */
	private Brand brand;
	
    /**
     * Product Category
     */
	private ProductCategory productCategory;
	
	private String image;
	
	private Set<ProductDetail> details;

    @Id
	@GeneratedValue
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "alias", nullable = false, length = 32)
	public String getAlias() {
		return this.alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	@Column(name = "active", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(mappedBy = "product", cascade={CascadeType.REMOVE})
	public Set<ProductDetail> getDetails() {
		return this.details;
	}

	public void setDetails( Set<ProductDetail> details ) {
		this.details = details;
	}

	@Column(name = "price")
	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Column(name = "offer_price")
	public BigDecimal getOfferPrice() {
		return offerPrice;
	}

	public void setOfferPrice(BigDecimal offerPrice) {
		this.offerPrice = offerPrice;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "product_category", nullable = false)
	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "brand", nullable = true)
	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}


	@Column(length=255)
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Product o = (Product) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.alias, o.alias)
				.append(this.brand, o.brand)
				.append(this.image, o.image)
				.append(this.offerPrice, o.offerPrice)
				.append(this.price, o.price)
				.append(this.productCategory, o.productCategory)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)		
			.append(alias)
			.append(brand)
			.append(id)
			.append(image)
			.append(offerPrice)			
			.append(price)
			.append(productCategory)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}