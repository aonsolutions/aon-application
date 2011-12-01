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
@Table(name="warehouse_transfer_detail")
public class WarehouseTransferDetail implements ITransferObject {
	
	private static final long serialVersionUID = 8551949263373840332L;
	
	private Integer id;
	private WarehouseTransfer warehouseTransfer;
	private Item item;
	private double quantity;
	
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
	@JoinColumn(name="warehouse_transfer", nullable=false, updatable=false)
	public WarehouseTransfer getWarehouseTransfer() {
		return warehouseTransfer;
	}
	public void setWarehouseTransfer(WarehouseTransfer warehouseTransfer) {
		this.warehouseTransfer = warehouseTransfer;
	}
	
	@ManyToOne
	@JoinColumn(name="item", nullable=false)
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
		this.quantity = quantity;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final WarehouseTransferDetail o = (WarehouseTransferDetail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.warehouseTransfer, o.warehouseTransfer)
				.append(this.item, o.item)
				.append(this.quantity, o.quantity)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(this.warehouseTransfer)
			.append(this.item)
			.append(this.quantity)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}