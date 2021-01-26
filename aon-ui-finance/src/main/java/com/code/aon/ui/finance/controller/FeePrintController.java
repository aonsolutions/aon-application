package com.code.aon.ui.finance.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Scope;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;


public class FeePrintController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<ITransferObject> orderedList;
	

	public void changeStatus(ValueChangeEvent event) throws ManagerBeanException {
		Object value = event.getNewValue();
		
		if(CustomerStatus.ACTIVE.name().equalsIgnoreCase(value.toString()))
			FeeExportGwtController.setStatus(CustomerStatus.ACTIVE.ordinal());
		else if(CustomerStatus.INACTIVE.name().equalsIgnoreCase(value.toString()))
			FeeExportGwtController.setStatus(CustomerStatus.INACTIVE.ordinal());
		else if(CustomerStatus.BLOCKED.name().equalsIgnoreCase(value.toString()))
			FeeExportGwtController.setStatus(CustomerStatus.BLOCKED.ordinal());
		
		super.addEqualExpression(event);
	}	
	
	public void changeScope(ValueChangeEvent event) throws ManagerBeanException {
		Scope value = (Scope) event.getNewValue();
		FeeExportGwtController.setScope(value.getId());
		super.addIdEqualExpression(event);
	}	
	
	public List<ITransferObject> getOrderedList() {
		return orderedList;
	}

	public void setOrderedList(List<ITransferObject> orderedList) {
		this.orderedList = orderedList;
	}
	
	public void onOrderCustomerFeeByDate(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_BILLING_DATE));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderCustomerFeeByItem(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_ITEM_ID));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}
	
	public void onOrderCustomerFeeByCustomer(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = getCriteria();
		criteria.setOrderByList(null);
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_ID));
		criteria.addOrder(getManagerBean().getFieldName(IEntityAlias.CUSTOMER_FEE_CUSTOMER_REGISTRY_NAME));
		orderedList=getManagerBean().getList(criteria);
	}

}