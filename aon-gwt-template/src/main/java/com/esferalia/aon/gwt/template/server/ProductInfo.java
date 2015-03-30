package com.esferalia.aon.gwt.template.server;

import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.google.gwt.user.client.rpc.IsSerializable;

public class ProductInfo implements IsSerializable{

	Product product;
	Item item;
	Integer row;
	Boolean isProduct;
	
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Integer getRow() {
		return row;
	}
	public void setRow(Integer row) {
		this.row = row;
	}
	public Boolean getIsProduct() {
		return isProduct;
	}
	public void setIsProduct(Boolean isProduct) {
		this.isProduct = isProduct;
	}
	
	
}
