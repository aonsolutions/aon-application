package com.code.aon.ui.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryDetailSource;
import com.code.aon.warehouse.enumeration.DeliveryDetailType;

public class DeliveryDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			deliveryDetail.setLine(calculateNextLine((Delivery)controller.getMasterController().getTo()));
			deliveryDetail.setType(DeliveryDetailType.MANUAL);
			deliveryDetail.setSource(DeliveryDetailSource.DIRECT);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();
		deliveryDetail.setWarehouse(((DeliveryController)deliveryDetailController.getMasterController()).getWarehouse());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailController.getTo();
		deliveryDetail.setWarehouse(((DeliveryController)deliveryDetailController.getMasterController()).getWarehouse());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();

		controller.setLongDescription((deliveryDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Projection projection = Projection.max(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		Object value = deliveryDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}