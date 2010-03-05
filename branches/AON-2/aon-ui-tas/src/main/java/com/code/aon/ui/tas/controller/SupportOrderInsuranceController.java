package com.code.aon.ui.tas.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.tas.Appraiser;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.SupportOrderInsurance;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class SupportOrderInsuranceController extends BasicController {
	
	private static final String SUPPORT_ORDER_CONTROLLER = "SupportOrder";
	
	@SuppressWarnings("unchecked")
	public void onInsurance(ActionEvent event) throws ManagerBeanException{
		SupportOrderController supportOrderController = (SupportOrderController)AonUtil.getController(SUPPORT_ORDER_CONTROLLER);
		SupportOrder supportOrder = (SupportOrder)supportOrderController.getTo();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(this.getFieldName(ITASAlias.SUPPORT_ORDER_INSURANCE_SUPPORT_ORDER_ID), supportOrder.getId());
		Iterator iter = this.getManagerBean().getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			SupportOrderInsurance supportOrderInsurance = (SupportOrderInsurance)iter.next();
			if(supportOrderInsurance.getAppraiser() == null){
				Appraiser appraiser = new Appraiser();
				appraiser.setRegistry(new Registry());
				supportOrderInsurance.setAppraiser(appraiser);
			}
			this.setTo(supportOrderInsurance);
		}else{
			this.onReset(null);
			((SupportOrderInsurance)this.getTo()).setSupportOrder(supportOrder);;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void customerData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), event.getNewValue());
			Iterator iter = customerBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				((SupportOrderInsurance)this.getTo()).setInsurance((Customer)iter.next());
			}
		}
	}
}