package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;


public class ShopController {
	
	private String backView;
	private String contentView;
	private boolean itemsView;
	private boolean detailView;
	private boolean cartView;
	private boolean logged;
	

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

	public boolean isItemsView() {
		return itemsView;
	}

	public void setItemsView(boolean itemsView) {
		if (itemsView == true) {
			setDetailView(false);
			setCartView(false);
		}
		this.itemsView = itemsView;
	}

	public boolean isDetailView() {
		return detailView;
	}

	public void setDetailView(boolean detailView) {
		if (detailView == true) {
			setItemsView(false);
			setCartView(false);
		}
		this.detailView = detailView;
	}

	public boolean isCartView() {
		return cartView;
	}

	public void setCartView(boolean cartView) {
		if (cartView == true) {
			setDetailView(false);
			setItemsView(false);
		}
		this.cartView = cartView;
	}

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	
	public void refreshView(ActionEvent event) {
		if (getBackView()==null) {
			setItemsView(true);
		} else if (getBackView().equals(IECommerceConstants.ITEM_DETAIL_VIEW)) {
			setDetailView(true);
		} else if (getBackView().equals(IECommerceConstants.ITEM_LIST_VIEW)) {
			setItemsView(true);
		} else if (getBackView().equals(IECommerceConstants.SHOPPING_CART_VIEW)) {
			setCartView(true);
		} else {
			setItemsView(true);
		}
	}
	
	public void onViewCart(ActionEvent event) {
		setCartView(true);
		setBackView(IECommerceConstants.ITEM_LIST_VIEW);
		
//		((ShopController) AonUtil
//				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
//				.setCart(true);
//		((ShopController) AonUtil
//				.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
//				.setBackView(IECommerceConstants.ITEM_LIST_VIEW);		
	}
	
}
