package com.code.aon.product;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.code.aon.account.Account;
import com.esferalia.aon.entity.master.ProductDB;

@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames="code"))
public class Product extends ProductDB {
	
	private static final long serialVersionUID = 1L;

    private Account salesAccount;
    private Account purchaseAccount;
	private Set<Item> items = new HashSet<Item>();

	@Transient
	public Account getSalesAccount() {
		return salesAccount;
	}

	public void setSalesAccount(Account salesAccount) {
		this.salesAccount = salesAccount;
	}

	@Transient
	public Account getPurchaseAccount() {
		return purchaseAccount;
	}

	public void setPurchaseAccount(Account purchaseAccount) {
		this.purchaseAccount = purchaseAccount;
	}

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

}