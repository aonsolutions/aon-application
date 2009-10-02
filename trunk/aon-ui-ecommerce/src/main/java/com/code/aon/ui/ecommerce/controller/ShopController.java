package com.code.aon.ui.ecommerce.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;


public class ShopController {
	
	private boolean logged;
	private ViewEnum backView;
	private ViewEnum contentView;
	
	public ShopController() {
		setBackView(ViewEnum.ITEM_LIST);
		ConfigController cc = (ConfigController)AonUtil.getRegisteredBean(IECommerceConstants.CONFIG_CONTROLLER);
		if(cc.isWelcomeBanner()){
			setContentView(ViewEnum.WELCOME);
		} else {
			setContentView(ViewEnum.ITEM_LIST);
		}
	}

	public ViewEnum getBackView() {
		return backView;
	}
	public void setBackView(ViewEnum backView) {
		this.backView = backView;
	}
	public String backView() {
		return backView.getOutcome();
	}
	
	public ViewEnum getContentView() {
		return contentView;
	}
	public void setContentView(ViewEnum contentView) {
		this.contentView = contentView;
	}
	public String contentView() {
		return contentView.getOutcome();
	}

	public boolean isItemsView() {
		return getContentView() == ViewEnum.ITEM_LIST;
	}
	public boolean isDetailView() {
		return getContentView() == ViewEnum.ITEM_DETAIL;
	}
	public boolean isCartView() {
		return getContentView() == ViewEnum.SHOPPING_CART;
	}
	public boolean isWelcomeView() {
		return getContentView() == ViewEnum.WELCOME;
	}

	public boolean isLogged() {
		return logged;
	}
	public void setLogged(boolean logged) {
		this.logged = logged;
	}
	
	public void refreshView(ActionEvent event) {
		ViewEnum newContentView = (getBackView()==null)?ViewEnum.ITEM_LIST:getBackView(); 
		setBackView( getContentView() );
		setContentView(newContentView);
	}
	
	public void onViewCart(ActionEvent event) {
		setContentView(ViewEnum.SHOPPING_CART);
		setBackView(ViewEnum.ITEM_LIST);
	}
	
	public void goList(ActionEvent event) {
		setContentView(ViewEnum.ITEM_LIST);
	}
	
}
