package com.code.aon.purchase;

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
import com.code.aon.supplier.Supplier;

@Entity
@Table(name="item_supplier")
public class ItemSupplier implements ITransferObject{
	
	private static final long serialVersionUID = -6415420632921077499L;

	private Integer id;
	private Item item;
	private Supplier supplier;
	private String code;
	private Integer priority;

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
	@JoinColumn( name="supplier",nullable=false )
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemSupplier o = (ItemSupplier) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(this.supplier, o.supplier)
				.append(this.priority, o.priority)
				.append(this.code, o.code)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(item)			
			.append(supplier)
			.append(priority)
			.append(code)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}