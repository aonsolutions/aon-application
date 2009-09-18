package com.code.aon.ui.ecommerce.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.product.enumeration.AttachmentType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ecommerce.util.IECommerceConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ShopItemController extends BasicController {

	private Item item;
	private AonFile file;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public AonFile getFile() {
		return file;
	}

	public void setFile(AonFile file) {
		this.file = file;
	}

	public void addToCart(ActionEvent event) {
		((ShoppingCartController) AonUtil
				.getRegisteredBean(IECommerceConstants.SHOPPING_CART_CONTROLLER)).addToCart(item);
		((ShopController) AonUtil.getRegisteredBean(IECommerceConstants.SHOP_CONTROLLER))
				.setCartView(true);
	}

	/*
	 * public void paint(OutputStream out, Object data) throws IOException { //
	 * if (getAonFile() != null && (getAonFile().getSize() > 0) ) { //
	 * out.write(getAonFile().getData()); // } //
	 * out.write(((ItemAttachment)getTo()).getData());
	 * out.write(((ItemAttachment)getTo()).getData());
	 * //this.onSelectNext(null);
	 * 
	 * }
	 */
	public void paint(OutputStream out, Object data) throws IOException {
		try {
			IManagerBean attachBean = AonUtil.getManagerBean(ItemAttachment.class);
			Criteria criteria = new Criteria();
			String itemAlias = attachBean.getFieldName(IProductAlias.ITEM_ATTACHMENT_ITEM_ID);
			String typeAlias = attachBean.getFieldName(IProductAlias.ITEM_ATTACHMENT_TYPE);
			criteria.addEqualExpression(itemAlias, data);
			criteria.addEqualExpression(typeAlias, AttachmentType.THUMBNAIL);

			List<ITransferObject> list = attachBean.getList(criteria);
			if (list != null && list.size() > 0) {
				ItemAttachment attach = (ItemAttachment) list.get(0);
				out.write(attach.getData());
			}
		} catch (ManagerBeanException e) {
			// Nada. La foto no se ve y punto.
		}
	}

	public Integer getKey() {
		if (getTo() == null) {
			this.onSelectFirst(null);
		}
		return ((ItemAttachment) getTo()).getId();
	}

	@Override
	public DataModel getModel() throws ManagerBeanException {
		// TODO Auto-generated method stub
		return super.getModel();
	}

}
