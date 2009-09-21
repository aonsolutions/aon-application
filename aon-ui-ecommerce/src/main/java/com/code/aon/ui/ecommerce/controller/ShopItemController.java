package com.code.aon.ui.ecommerce.controller;

import java.io.OutputStream;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.util.AonUtil;

public class ShopItemController {

	private ShopItem item;
	
	public ShopItem getItem() {
		return item;
	}

	public void setItem(ShopItem item) {
		this.item = item;
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).addToCart(item);
		ShopController sc = (ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER);
		sc.setBackView( ViewEnum.ITEM_DETAIL);
		sc.setContentView( ViewEnum.SHOPPING_CART );
	}

	public void paintThumbnail(OutputStream out, Object data) {
		if (getItem().getThumbnail() != null) {
			getItem().paintThumbnail(out, data);
		}
	}

}
