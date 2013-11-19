package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.registry.controller.RegistryAddInfoController;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAddInfoSearchListener extends ControllerSearchListener {

	private Customer customer;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), getCustomer().getRegistry().getId());
		} else {
			RegistryAddInfoController controller = (RegistryAddInfoController)getController();
			if (controller.isCustomersOnly()) {
				criteria.addInExpression(getFieldName(IEntityAlias.REGISTRY_ADD_INFO_REGISTRY_ID), controller.getCustomersOnlyExpression());			
			}
		}
	}

}