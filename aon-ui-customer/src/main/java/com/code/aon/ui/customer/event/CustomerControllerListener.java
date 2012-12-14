package com.code.aon.ui.customer.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		CustomerController controller = (CustomerController)event.getController();
		controller.setShowAlumnData(false);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CustomerController controller = (CustomerController)event.getController();
		try {
			controller.setInvoicingGroup(obtainInvocingGroup((Customer)controller.getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private InvoicingGroup obtainInvocingGroup(Customer customer) throws ManagerBeanException {
		IManagerBean groupBean = BeanManager.getManagerBean(InvoicingGroup.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(groupBean.getFieldName(IEntityAlias.INVOICING_GROUP_PARENT_ID), customer.getRegistry().getId());
		Iterator<?> iterator = groupBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return (InvoicingGroup)iterator.next();
		}

		IManagerBean groupDetailBean = BeanManager.getManagerBean(InvoicingGroupDetail.class);
		criteria = new Criteria();
		criteria.addEqualExpression(groupDetailBean.getFieldName(IEntityAlias.INVOICING_GROUP_DETAIL_CHILD_ID), customer.getRegistry().getId());
		iterator = groupDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return ((InvoicingGroupDetail)iterator.next()).getInvoicingGroup();
		}

		return null;
	}

}
