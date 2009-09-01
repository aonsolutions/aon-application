package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ShopItemController extends BasicController {
	
	private Item item;

	
//	private AonFile aonFile;

	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
		try {
			refreshModel();
		} catch (ManagerBeanException e) {
			String msg = "La búsqueda falló";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e); 
		}
	}
	
//	public AonFile getAonFile() {
//		aonFile = new AonFile();
//		aonFile.setData(((ItemAttachment)getTo()).getData());
//		return aonFile;
//	}
//	
//	public void setAonFile(AonFile aonFile) {
//		this.aonFile = aonFile;
//	}
	
	private void refreshModel() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		String alias = IProductAlias.ITEM_ATTACHMENT_ITEM_ID;
		//String alias = "ItemAttachment.item.id";
		String identifier = AonUtil.getManagerBean(Product.class).getFieldName(alias);
		criteria.addEqualExpression(identifier, item.getId());
		
		this.clearCriteria();
		this.setCriteria(criteria);
		this.onSearch(null);
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
			.addToCart(item);
		((ShopController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
			.setCart(true);
	}
	
	public void paint(OutputStream out, Object data) throws IOException {
//		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
//			out.write(getAonFile().getData());
//		}
//		out.write(((ItemAttachment)getTo()).getData());
		this.onSelectFirst(null);
		out.write(((ItemAttachment)getTo()).getData());
		
	} 
	
//	public String getKey() {
//		return getTo().toString();
//	}

	
}
