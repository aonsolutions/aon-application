package com.code.aon.ui.sales.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesDetailSource;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.sales.controller.SalesDetailController;

public class SalesDetailControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();

		controller.setLongDescription(false);
		try {
			salesDetail.setLine(calculateNextLine((Sales)controller.getMasterController().getTo()));
			salesDetail.setStatus(SalesDetailStatus.PENDING);
			salesDetail.setSource(SalesDetailSource.DIRECT);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		controller.initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		SalesDetailController controller = (SalesDetailController)event.getController();
		SalesDetail salesDetail = (SalesDetail)controller.getTo();

		controller.setLongDescription((salesDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Sales sales) throws ManagerBeanException {
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID), sales.getId());
		Projection projection = Projection.max(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_LINE));
		Object value = salesDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}