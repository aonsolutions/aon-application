package com.code.aon.warehouse;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;

@Entity
@Table(name="item_warehouse")
public class ItemWarehouse implements ITransferObject{
	
	private static final long serialVersionUID = -6415420632921077499L;

	private Integer id;
	private Item item;
	private Warehouse warehouse;
	private Double stockMin;
	private Double stockMax;
	private String location;

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
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
	@ManyToOne
	@JoinColumn( name="warehouse",nullable=false )
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@Column(name="stock_min")
	public Double getStockMin() {
		return stockMin;
	}
	public void setStockMin(Double stockMin) {
		this.stockMin = stockMin;
	}
	
	@Column(name="stock_max")
	public Double getStockMax() {
		return stockMax;
	}
	public void setStockMax(Double stockMax) {
		this.stockMax = stockMax;
	}

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemWarehouse o = (ItemWarehouse) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(this.warehouse, o.warehouse)
				.append(this.stockMax, o.stockMax)
				.append(this.stockMin, o.stockMin)
				.append(this.location, o.location)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(item)			
			.append(warehouse)
			.append(stockMin)
			.append(stockMax)
			.append(location)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}