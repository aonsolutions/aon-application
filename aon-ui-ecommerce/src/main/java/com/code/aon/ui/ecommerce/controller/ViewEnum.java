package com.code.aon.ui.ecommerce.controller;

public enum ViewEnum {

	
	ITEM_LIST("items"),
	ITEM_DETAIL("detail"),
	SHOPPING_CART("cart");

	private String outcome;
	
	private ViewEnum(String outcome) {
		this.outcome = outcome;
	}

	public String getOutcome() {
		return outcome;
	}

	@Override
	public String toString() {
		return outcome;
	}
}
