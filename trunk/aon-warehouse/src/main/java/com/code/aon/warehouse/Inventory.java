package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="inventory")
public class Inventory implements ITransferObject {
	
	private static final long serialVersionUID = 5398894889661223054L;

	private Integer id;
	private Date inventoryDate;
	private Warehouse warehouse;
	private String description;

	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="inventory_date")
	public Date getInventoryDate() {
		return inventoryDate;
	}
	public void setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	@ManyToOne
	@JoinColumn( name="warehouse",nullable=false )
	public Warehouse getWarehouse() {
		return warehouse;
	}
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	@Column(name="description", length=64)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Inventory o = (Inventory) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.inventoryDate, o.inventoryDate)
				.append(this.description, o.description)
				.append(this.warehouse, o.warehouse)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(inventoryDate)
			.append(description)			
			.append(warehouse)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
	
}