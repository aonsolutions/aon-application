package com.code.aon.ui.warehouse.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.resources.Employee;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailLabourController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.DeliveryDetailLabour;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryDetailControllerLabourListener extends ControllerAdapter {

	private static final String DELIVERY_CONTROLLER_NAME = "delivery";
	
	private static final String DELIVERY_DETAIL_LABOUR_CONTROLLER_NAME = "deliveryDetailLabour";
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
			DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
			createDeliveryDetailLabour(deliveryDetail, deliveryController.getEmployeeId());
			reloadDetailLabourController((Delivery)deliveryController.getTo());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
			removeDeliveryDetailLabour(deliveryDetail);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
			DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
			createDeliveryDetailLabour(deliveryDetail, deliveryController.getEmployeeId());
			reloadDetailLabourController((Delivery)deliveryController.getTo());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			DeliveryDetail deliveryDetail = (DeliveryDetail)event.getController().getTo();
			removeDeliveryDetailLabour(deliveryDetail);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)throws ControllerListenerException {
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		reloadDetailLabourController((Delivery)deliveryController.getTo());
	}

	private void createDeliveryDetailLabour(DeliveryDetail deliveryDetail, Integer employeeId) throws ManagerBeanException {
		if(deliveryDetail.getItem().getProduct().getId() != null && deliveryDetail.getItem().getProduct().getType().equals(ProductType.LABOUR)){
			DeliveryDetailLabour detailLabour = new DeliveryDetailLabour();
			detailLabour.setDeliveryDetail(deliveryDetail);
			detailLabour.setQuantity(deliveryDetail.getQuantity());
			if(employeeId != null){
				detailLabour.setEmployee(new Employee());
				detailLabour.getEmployee().setId(employeeId);
			}
			IManagerBean detailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			detailLabourBean.insert(detailLabour);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void removeDeliveryDetailLabour(DeliveryDetail deliveryDetail) throws ManagerBeanException {
		IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_ID), deliveryDetail.getId());
		Iterator iter = deliveryDetailLabourBean.getList(criteria, 0, 1).iterator();
		if(iter.hasNext()){
			DeliveryDetailLabour deliveryDetailLabour = (DeliveryDetailLabour)iter.next();
			deliveryDetailLabourBean.remove(deliveryDetailLabour);
		}
	}
	
	private void reloadDetailLabourController(Delivery delivery) {
		DeliveryDetailLabourController detailLabourController = (DeliveryDetailLabourController)AonUtil.getController(DELIVERY_DETAIL_LABOUR_CONTROLLER_NAME);
		detailLabourController.reloadDeliveryDetailLabours(delivery);
	}
}