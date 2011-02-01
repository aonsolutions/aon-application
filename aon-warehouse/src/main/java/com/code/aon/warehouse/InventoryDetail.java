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
@Table(name="inventory_detail")
public class InventoryDetail implements ITransferObject {
	
	private static final long serialVersionUID = -8584837639559704341L;

	private Integer id;
	private Inventory inventory;
	private Item item;
	private double actualQuantity;
	private double realQuantity;
	private double cost;

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
    @JoinColumn(name="inventory", nullable = false, updatable = false)
	public Inventory getInventory() {
		return inventory;
	}
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	@ManyToOne
    @JoinColumn(name="item", nullable = false, updatable = false)
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}

	@Column(name="actual_quantity")
	public double getActualQuantity() {
		return actualQuantity;
	}
	public void setActualQuantity(double actualQuantity) {
		this.actualQuantity = actualQuantity;
	}
	
	@Column(name="real_quantity")
	public double getRealQuantity() {
		return realQuantity;
	}
	public void setRealQuantity(double realQuantity) {
		this.realQuantity = realQuantity;
	}

	@Column(name="cost")
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final InventoryDetail o = (InventoryDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(this.actualQuantity, o.actualQuantity)
				.append(this.cost, o.cost)
				.append(this.realQuantity, o.realQuantity)
				.append(this.inventory, o.inventory)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(item)			
			.append(actualQuantity)
			.append(cost)
			.append(realQuantity)
			.append(inventory)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}