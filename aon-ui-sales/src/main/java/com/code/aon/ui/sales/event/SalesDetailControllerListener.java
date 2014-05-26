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
import com.code.aon.ui.sales.controller.SalesDetailController;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		checkQuantities();
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		checkQuantities();
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			salesDetail.setLine(calculateNextLine((Sales)controller.getMasterController().getTo()));
			salesDetail.setStatus(SalesDetailStatus.PENDING);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
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

	private	Integer calculateNextLine(Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		Projection projection = Projection.max(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}
	
	private void checkQuantities() throws ControllerListenerException {
		Sales sales = (Sales) ((LinesController)this.getController()).getMasterController().getTo();
		SalesDetail salesDetail = (SalesDetail) this.getController().getTo();
		if (salesDetail.getQuantity() < 0 && !sales.isItemReturn()) {
			throw new ControllerListenerException("La cantidad no puede ser negativa.");
		}
		if (salesDetail.getQuantity() > 0 && sales.isItemReturn()) {
			throw new ControllerListenerException("La cantidad debe ser negativa.");
		}
	}

}