package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ShopItemController extends BasicController {
	
	private Item item;
	private ItemAttachment itemThumbnail;
	private AonFile file;

	
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
	
	public ItemAttachment getItemThumbnail() {
		return itemThumbnail;
	}

	public void setItemThumbnail(ItemAttachment itemThumbnail) {
		this.itemThumbnail = itemThumbnail;
	}

	public AonFile getFile() {
		return file;
	}

	public void setFile(AonFile file) {
		this.file = file;
	}
	
	private void refreshModel() throws ManagerBeanException{
		Criteria criteria = new Criteria();
		String alias = IProductAlias.ITEM_ATTACHMENT_ITEM_ID;
		String identifier = AonUtil.getManagerBean(ItemAttachment.class).getFieldName(alias);
		criteria.addEqualExpression(identifier, item.getId());
		
		this.clearCriteria();
		this.setCriteria(criteria);
		//this.setCriteria(null);
		this.onSearch(null);
		setItemThumbnail(null);
		if(this.getModel().getRowCount()>0){
			onSelectFirst(null);
			setItemThumbnail((ItemAttachment)getTo());
		}
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER))
			.addToCart(item);
		((ShopController) AonUtil
			.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
			.setCartView(true);
	}
	
	/*
	public void paint(OutputStream out, Object data) throws IOException {
//		if (getAonFile() != null && (getAonFile().getSize() > 0) ) {
//			out.write(getAonFile().getData());
//		}
//		out.write(((ItemAttachment)getTo()).getData());
		out.write(((ItemAttachment)getTo()).getData());
		//this.onSelectNext(null);
		
	} 
	
	 */
	public void paint(OutputStream out, Object data) throws IOException {
		try {
			Integer id = (Integer) data;
			ItemAttachment ia = (ItemAttachment) getManagerBean().get(id);
			if (ia != null && ia.getData() != null) {
				out.write(ia.getData());	
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		
	} 
	
	
	public Integer getKey() {
		if(getTo()==null){
			this.onSelectFirst(null);
		}
		return ((ItemAttachment)getTo()).getId();
	}
	
	@Override
	public DataModel getModel() throws ManagerBeanException {
		// TODO Auto-generated method stub
		return super.getModel();
	}




	
}
