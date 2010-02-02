package com.code.aon.ui.sales.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.dao.IProductAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.LinesController;

public class FeeInvoicingDetailController extends LinesController {
	
	private WorkPlace workPlace;
	
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	@SuppressWarnings("unchecked")
	public void itemData(ValueChangeEvent event) throws ManagerBeanException, ExpressionException{
		if(event.getNewValue() != null && !event.getNewValue().equals("")){
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(itemBean.getFieldName(IProductAlias.ITEM_ID), event.getNewValue().toString());
			Iterator iter = itemBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Item item = (Item)iter.next();
				((InvoiceDetail)this.getTo()).setItem(item);
			}
		}
	}
}