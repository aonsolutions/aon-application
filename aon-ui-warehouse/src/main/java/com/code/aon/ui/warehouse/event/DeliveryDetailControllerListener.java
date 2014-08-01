package com.code.aon.ui.warehouse.event;

import com.code.aon.AonVersion;
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
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryDetailControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			deliveryDetail.setLine(calculateNextLine((Delivery)controller.getMasterController().getTo()));
			deliveryDetail.setWarehouse(((DeliveryController)controller.getMasterController()).getWarehouse());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		deliveryDetail.setWarehouse(((DeliveryController)controller.getMasterController()).getWarehouse());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		deliveryDetail.setWarehouse(((DeliveryController)controller.getMasterController()).getWarehouse());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController controller = (DeliveryDetailController)event.getController();
		DeliveryDetail deliveryDetail = (DeliveryDetail)controller.getTo();
		deliveryDetail.setWarehouse(((DeliveryController)controller.getMasterController()).getWarehouse());

		controller.setLongDescription((deliveryDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Projection projection = Projection.max(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
		Object value = deliveryDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}