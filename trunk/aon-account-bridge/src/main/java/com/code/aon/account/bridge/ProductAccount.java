package com.code.aon.account.bridge;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.product.Product;
import com.esferalia.aon.entity.master.ProductAccountDB;

@Entity
@Table(name="product_account")
public class ProductAccount extends ProductAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getProduct();
	}
	public void setLinkedTo(ITransferObject to) {
		setProduct((Product) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getProduct()==null) ? null : getProduct().getName();
	}

}