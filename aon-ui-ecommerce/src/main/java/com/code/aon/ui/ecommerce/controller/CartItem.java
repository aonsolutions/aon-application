package com.code.aon.ui.ecommerce.controller;

import com.code.aon.product.Item;

public class CartItem {
	private Item item;
	private double quantity;
	private double total;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

}
