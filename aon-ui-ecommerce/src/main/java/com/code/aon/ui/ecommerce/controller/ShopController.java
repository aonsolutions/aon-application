package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;


public class ShopController {
	
	private String backView;
	private String contentView;
	private boolean items;
	private boolean detail;
	private boolean cart;
	

	public String getBackView() {
		return backView;
	}
	
	public void setBackView(String backView) {
		this.backView = backView;
	}

	public String backView() {
		return backView;
	}
	
	public String getContentView() {
		return contentView;
	}

	public void setContentView(String contentView) {
		this.contentView = contentView;
	}

	public boolean isItems() {
		return items;
	}

	public void setItems(boolean items) {
		if (items == true) {
			setDetail(false);
			setCart(false);
		}
		this.items = items;
	}

	public boolean isDetail() {
		return detail;
	}

	public void setDetail(boolean detail) {
		if (detail == true) {
			setItems(false);
			setCart(false);
		}
		this.detail = detail;
	}

	public boolean isCart() {
		return cart;
	}

	public void setCart(boolean cart) {
		if (cart == true) {
			setDetail(false);
			setItems(false);
		}
		this.cart = cart;
	}
	
	public void refreshView(ActionEvent event) {
		if (getBackView()==null) {
			setItems(true);
		} else if (getBackView().equals(IECommerceConstants.ITEM_DETAIL_VIEW)) {
			setDetail(true);
		} else if (getBackView().equals(IECommerceConstants.ITEM_LIST_VIEW)) {
			setItems(true);
		} else if (getBackView().equals(IECommerceConstants.SHOPPING_CART_VIEW)) {
			setCart(true);
		} else {
			setItems(true);
		}
	}
	
	public void onViewCart(ActionEvent event) {
		setCart(true);
		setBackView(IECommerceConstants.ITEM_LIST_VIEW);
		
//		((ShopController) AonUtil
//				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
//				.setCart(true);
//		((ShopController) AonUtil
//				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
//				.setBackView(IECommerceConstants.ITEM_LIST_VIEW);		
	}
	
}
