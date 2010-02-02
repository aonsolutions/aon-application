package com.code.aon.ui.warehouse.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetailLabour;
import com.code.aon.warehouse.LabourReportTo;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class LabourReportController implements ICollectionProvider{
	
	private static final Logger LOGGER = Logger.getLogger(LabourReportController.class.getName());
	
	private ListDataModel model;
	
	private Criteria criteria;

	public ListDataModel getModel() {
		return model;
	}

	public void setModel(ListDataModel model) {
		this.model = model;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void onEditSearch(MenuEvent event){
		this.criteria = new Criteria();
	}

	public void onEditSearch(ActionEvent event){
		this.criteria = new Criteria();
	}

	public void addEmployeeExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue()!=null){
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			this.getCriteria().addEqualExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_EMPLOYEE_ID), event.getNewValue());
		}
	}
	
	public void addCustomerExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue()!=null && !"".equals(((String)event.getNewValue()).trim())){
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			this.getCriteria().addEqualExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_DELIVERY_CUSTOMER_ID), event.getNewValue());
		}
	}
	
	public void addFromExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue()!=null){
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			this.getCriteria().addGreaterThanOrEqualExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_DELIVERY_ISSUE_TIME), event.getNewValue());
		}
	}
	
	public void addToExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue()!=null){
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			this.getCriteria().addLessThanOrEqualExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_DELIVERY_ISSUE_TIME), event.getNewValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadCollection(ActionEvent event){
		List<LabourReportTo> labourReportList = new LinkedList<LabourReportTo>();
		try {
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			this.getCriteria().addOrder(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_EMPLOYEE_ID));
			Iterator iter = deliveryDetailLabourBean.getList(this.getCriteria()).iterator();
			Integer currentEmployeeId = null;
			LabourReportTo currentTo = null;
			while(iter.hasNext()){
				DeliveryDetailLabour labour = (DeliveryDetailLabour)iter.next();
				if(currentEmployeeId != labour.getEmployee().getId()){
					if(currentEmployeeId != null){
						labourReportList.add(currentTo);
					}
					currentTo = new LabourReportTo();
					currentTo.setEmployee(labour.getEmployee());
					currentEmployeeId = labour.getEmployee().getId();
				}
				currentTo.setRealHours(currentTo.getRealHours() + labour.getQuantity());
				currentTo.setEstimatedHours(currentTo.getEstimatedHours() + labour.getDeliveryDetail().getQuantity());
			}
			if(currentTo != null){
				labourReportList.add(currentTo);
			}
			setModel(new ListDataModel(labourReportList));
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		return (List)getModel().getWrappedData();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}
}