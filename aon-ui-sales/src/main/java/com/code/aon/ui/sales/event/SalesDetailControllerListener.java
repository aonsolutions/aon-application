package com.code.aon.ui.sales.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.SalesController;
import com.code.aon.ui.sales.controller.SalesDetailController;
import com.code.aon.ui.sales.util.SalesUtils;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();
		Sales sales = (Sales)controller.getMasterController().getTo();

		controller.setLongDescription(false);
		try {
			salesDetail.setLine(calculateNextLine(sales));
			salesDetail.setStatus(SalesDetailStatus.PENDING);
			salesDetail.setDeliveryDate(sales.getDeliveryDate());
			salesDetail.setCarrier((sales.getCarrier() != null && sales.getCarrier().getId() != null) ? sales.getCarrier() : null);
			salesDetail.getSales().setWorkPlace(sales.getWorkPlace());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesController salesController = (SalesController)((LinesController)event.getController()).getMasterController();
		Sales sales = (Sales) salesController.getTo();
		SalesDetail salesDetail = (SalesDetail)event.getController().getTo();
		salesDetail.setDeliveryDate(sales.getDeliveryDate());
		salesDetail.setCarrier((sales.getCarrier() != null && sales.getCarrier().getId() != null) ? sales.getCarrier() : null);
		checkQuantities(sales, salesDetail);
		checkSerializable(salesDetail);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Sales sales = (Sales)((LinesController)event.getController()).getMasterController().getTo();
		SalesDetail salesDetail = (SalesDetail)event.getController().getTo();
		checkQuantities(sales, salesDetail);
		checkSerializable(salesDetail);
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		SalesDetail salesDetail = (SalesDetail)event.getController().getTo();
		SalesUtils utils = new SalesUtils();
		if(utils.isElaborationDone(salesDetail)){
			throw new ControllerListenerException("No se puede borrar, el producto está en proceso de elaboración.");
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();

		controller.setLongDescription(salesDetail.getDescription().length() > 64);
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		controller.loadPurchaseDetailMap();
		controller.loadElaborationMap();
	}
	
	private	Integer calculateNextLine(Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		Projection projection = Projection.max(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}
	
	private void checkQuantities(Sales sales, SalesDetail salesDetail) throws ControllerListenerException {
		if (salesDetail.getQuantity() < 0 && !sales.isItemReturn()) {
			throw new ControllerListenerException("La cantidad no puede ser negativa.");
		}
		if (salesDetail.getQuantity() > 0 && sales.isItemReturn()) {
			throw new ControllerListenerException("La cantidad debe ser negativa.");
		}
	}

	private void checkSerializable(SalesDetail salesDetail) throws ControllerListenerException {
		try {
			if (salesDetail.getItem() != null && salesDetail.getItem().getProduct().isSerializable()) {
				if (!salesDetail.getItem().getProduct().isLotable() && !salesDetail.getItem().isWildCard() && Math.abs(salesDetail.getQuantity()) != 1) {
					salesDetail.setQuantity(1);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}