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

public class DeliveryControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		((Delivery)controller.getTo()).setStatus(DeliveryStatus.PENDING);
		controller.setAddresses(null);
		controller.setWarehouse(null);
		controller.setDefaultPayMethod(null);
		controller.resetDeliveryPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryController controller = (DeliveryController)event.getController();
		try {
			controller.loadAddresses(((Delivery)controller.getTo()).getCustomer().getRegistry().getId());
	        controller.setWarehouse(obtainWarehouseId((Delivery)controller.getTo()));
			controller.loadDefaultPayMethod(((Delivery)controller.getTo()).getCustomer().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private Warehouse obtainWarehouseId(Delivery delivery) throws ControllerListenerException {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator iterator = deliveryDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return ((DeliveryDetail)iterator.next()).getWarehouse();
			} else {
				IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
				criteria = new Criteria();
				criteria.addOrder(warehouseBean.getFieldName(IWarehouseAlias.WAREHOUSE_NAME));
				Iterator<?> iter = warehouseBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					return (Warehouse)iter.next();
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
		return null;
	}

}