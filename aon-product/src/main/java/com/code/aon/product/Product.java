package com.code.aon.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.ProductDB;

@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames="code"))
@Heritable
public class Product extends ProductDB {
	
	private static final long serialVersionUID = 1L;

	private Set<Item> items = new HashSet<Item>();

    @OneToMany(mappedBy="product")
	public Set<Item> getItems() {
		return this.items;
	}
	
	public void setItems( Set<Item> items ) {
		this.items = items;
	}
	
	@Transient
	public void addItems(Item item) {
		item.setProduct( this );
		this.items.add( item );
	}

	@Transient
	public boolean isWithholding() {
		return (getRetention() != null && getRetention().getId() != null);
	}

}