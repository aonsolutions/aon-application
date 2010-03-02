package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryDetailLabourController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.DeliveryDetailLabour;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliveryControllerLabourListener extends ControllerAdapter {
	
	private static final String DELIVERY_DETAIL_LABOUR_CONTROLLER_NAME = "deliveryDetailLabour";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailLabourController detailLabourController = (DeliveryDetailLabourController)AonUtil.getController(DELIVERY_DETAIL_LABOUR_CONTROLLER_NAME);
		detailLabourController.reloadDeliveryDetailLabours((Delivery)event.getController().getTo());
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)throws ControllerListenerException {
		DeliveryDetailLabourController detailLabourController = (DeliveryDetailLabourController)AonUtil.getController(DELIVERY_DETAIL_LABOUR_CONTROLLER_NAME);
		detailLabourController.reloadDeliveryDetailLabours((Delivery)event.getController().getTo());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean deliveryDetailLabourBean = BeanManager.getManagerBean(DeliveryDetailLabour.class);
			Criteria criteria = new Criteria();
			Iterator iter = obtainDeliveryDetailList((Delivery)event.getController().getTo()).iterator();
			while(iter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)iter.next();
				criteria.addOrExpression(deliveryDetailLabourBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LABOUR_DELIVERY_DETAIL_ID), deliveryDetail.getId().toString());
			}
			Iterator labourIter = deliveryDetailLabourBean.getList(criteria).iterator();
			while(labourIter.hasNext()){
				deliveryDetailLabourBean.remove((DeliveryDetailLabour)labourIter.next());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
		
	}

	@SuppressWarnings("unchecked")
	private List obtainDeliveryDetailList(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		return deliveryDetailBean.getList(criteria);
	}
}
