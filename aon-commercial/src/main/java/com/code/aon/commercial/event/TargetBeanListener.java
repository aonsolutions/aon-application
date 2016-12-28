package com.code.aon.commercial.event;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.customer.Customer;

public class TargetBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		Target target = (Target)event.getTo();
		if (!target.isSkipUpdateCustomer()) {
			updateCustomer(target);
		}
	}

	private Customer updateCustomer(Target target) throws ManagerBeanException {
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Customer customer = (Customer)customerBean.get(target.getRegistry().getId());
		if (customer != null) {
			customer.setTariff(target.getTariff());
			customer.setSurcharge(target.isSurcharge());
			customer.setWithholding(target.isWithholding());
			customer.setTransaction(target.getTransaction());
			customer.setSkipUpdateTarget(true);
			customer = (Customer)customerBean.update(customer);
			customer.setSkipUpdateTarget(false);
		}
		return customer;
	}

}
