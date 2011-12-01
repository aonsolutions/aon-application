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
import com.code.aon.common.util.CommonUtil;

@Entity
@Table(name="catalogue_item")
public class CatalogueItem implements ITransferObject {

	private static final long serialVersionUID = 9026585458569586807L;

	private Integer id;
	private Catalogue catalogue;
	private Item item;
	private double quantity;
	private double price;
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
	@ForeignKey(name = "FK_CATALOGUE_ITEM_CATALOGUE")
	@Index(name = "IDX_CATALOGUE_ITEM_CATALOGUE")		
	public Catalogue getCatalogue() {
		return catalogue;
	}

	public void setCatalogue(Catalogue catalogue) {
		this.catalogue = catalogue;
	}

	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="item", nullable=false)
	@ForeignKey(name = "FK_CATALOGUE_ITEM_ITEM")
	@Index(name = "IDX_CATALOGUE_ITEM_ITEM")	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

    @Column(precision=15, scale=3)
	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
        this.quantity = CommonUtil.round(quantity, 3);
	}

    @Column(precision=15, scale=4)
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
        this.price = CommonUtil.round(price, 4);
	}

	@Column(precision = 6, scale = 2)
	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = CommonUtil.round(discount);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CatalogueItem o = (CatalogueItem) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.catalogue, o.catalogue)				
				.append(this.discount, o.discount)				
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
			.append(this.catalogue)
			.append(this.discount)
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
