package com.code.aon.ui.warehouse.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * A listener for DeliveryController
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-jun-2006
 * @since 1.0
 * 
 */
public class DeliveryControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		((Delivery)controller.getTo()).setStatus(DeliveryStatus.PENDING);
		controller.setAddresses(null);
		controller.setWarehouse(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		try {
			controller.loadAddresses(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
        controller.setWarehouse(obtainWarehouseId((Delivery)controller.getTo()));
	}
	
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
/*
		try {
			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
			Delivery delivery = (Delivery) event.getController().getTo();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID),delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				DeliveryDetail deliveryDetail = (DeliveryDetail) iter.next();
				Sales sales = deliveryDetail.getSalesDetail().getSales();
				sales.setDiscountExpression(new DiscountExpression("0.0"));
				sales.setIssueDate(deliveryDetail.getDelivery().getIssueTime());
				sales.setNumber(deliveryDetail.getDelivery().getNumber());
				sales.setPayMethod(null);
				sales.setSecurityLevel(deliveryDetail.getDelivery().getSecurityLevel());
				sales.setSeries(deliveryDetail.getDelivery().getSeries());
				sales.setCustomer(deliveryDetail.getDelivery().getCustomer());
				sales.setShippingAddress(delivery.getRaddress());
				salesBean.update(sales);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
*/
	}

	@SuppressWarnings("unchecked")
	private Warehouse obtainWarehouseId(Delivery delivery) throws ControllerListenerException {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return ((DeliveryDetail)iter.next()).getWarehouse();
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
		return null;
	}

}