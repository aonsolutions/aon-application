package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class PurchaseDirectInvoicingDetailController extends LinesController {

	@SuppressWarnings("unchecked")
	public void itemData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IProductAlias.ITEM_ID),event.getNewValue());
			Iterator iter = itemBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Item item = (Item)iter.next();
				((InvoiceDetail)this.getTo()).setItem(item);
			}
		}
	}
}