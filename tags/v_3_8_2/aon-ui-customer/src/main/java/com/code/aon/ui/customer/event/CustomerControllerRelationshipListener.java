package com.code.aon.ui.customer.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.registry.RegistryRelationship;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerControllerRelationshipListener extends ControllerAdapter {
	
	private static final String REGISTRY_RELATIONSHIP_CONTROLLER_NAME = "rRelationship";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		applyCustomerCriteria(event);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		applyCustomerCriteria(event);
	}

	private void applyCustomerCriteria(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		BasicController rRelationshipController = (BasicController)FormUtil.getController(REGISTRY_RELATIONSHIP_CONTROLLER_NAME);
		try {
			IManagerBean rRelationshipBean = BeanManager.getManagerBean(RegistryRelationship.class);
			rRelationshipController.clearCriteria();
			rRelationshipController.getCriteria().addEqualExpression(rRelationshipBean.getFieldName(IRegistryAlias.REGISTRY_RELATIONSHIP_REGISTRY_ID), customer.getId());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}
