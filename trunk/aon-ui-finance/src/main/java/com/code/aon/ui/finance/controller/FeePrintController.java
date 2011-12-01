package com.code.aon.ui.finance.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;


public class FeePrintController extends BasicController {
	
	private List<?> orderedList;
	
	@SuppressWarnings("unchecked")
	public List getOrderedList() {
		return orderedList;
	}

	@SuppressWarnings("unchecked")
	public void setOrderedList(List orderedList) {
		this.orderedList = orderedList;
	}
	
	public void onOrderCustomerFeeByDate(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_BILLING_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderCustomerFeeByItem(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_ITEM_ID));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderCustomerFeeByCustomer(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_ID));
		criteria.addOrder(getManagerBean().getFieldName(IFinanceAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}

}