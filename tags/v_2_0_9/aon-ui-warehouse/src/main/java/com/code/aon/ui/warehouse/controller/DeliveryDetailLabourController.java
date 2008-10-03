package com.code.aon.ui.warehouse.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.company.resources.Employee;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.DeliveryDetailLabour;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryDetailLabourController extends BasicController {
	
	private static final String DELIVERY_CONTROLLER_NAME = "delivery"; 

	@SuppressWarnings("unchecked")
	public int getRelatedDeliveryDetailRowIndex() throws ManagerBeanException{
		DeliveryDetailLabour detailLabour = (DeliveryDetailLabour)this.getModel().getRowData();
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Iterator iter = obtainDeliveryDetailList((Delivery)deliveryController.getTo()).iterator();
		int index = 0;
		while(iter.hasNext()){
			DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
			if(deliveryDetail.getId().equals(detailLabour.getDeliveryDetail().getId())){
				return index;
			}
			index++;
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public void reloadDeliveryDetailLabours(Delivery delivery) {
		try {
			Criteria criteria = new Criteria();
			Iterator iter = obtainDeliveryDetailList(delivery).iterator();
			if(iter.hasNext()){
				while(iter.hasNext()){
					DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
					criteria.addOrExpression(this.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_ID), deliveryDetail.getId().toString());
				}
			}else{
				criteria.addNullExpression(this.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_ID));
			}
			this.setCriteria(criteria);
			this.onSearch(null);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		} catch (ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private List obtainDeliveryDetailList(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		return deliveryDetailBean.getList(criteria);
	}
	
	@SuppressWarnings("unchecked")
	public void onEmployeeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean employeeBean = BeanManager.getManagerBean(Employee.class);
			Criteria criteria  = new Criteria();
			criteria.addEqualExpression(employeeBean.getFieldName(ICompanyAlias.EMPLOYEE_ID), event.getNewValue());
			Iterator iter = employeeBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				((DeliveryDetailLabour)this.getTo()).setEmployee((Employee)iter.next());
			}
		}
	}
}