package com.code.aon.commercial.event;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;

public class TargetFromCustomerBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		Customer customer = (Customer)event.getTo();
		insertOrUpdateTarget(customer);
	}

	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		Customer customer = (Customer)event.getTo();
		if (!customer.isSkipUpdateTarget()) {
			insertOrUpdateTarget(customer);
		}
	}

	private Target insertOrUpdateTarget(Customer customer) throws ManagerBeanException {
		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		Target target = (Target)targetBean.get(customer.getRegistry().getId());
		if (target == null) {
			target = new Target();
			target.setRegistry(customer.getRegistry());
			target.setTariff(customer.getTariff());
			target.setAdvertising(Advertising.ALLOWED);
			target.setSurcharge(customer.isSurcharge());
			target.setWithholding(customer.isWithholding());
			target.setTransaction(customer.getTransaction());
			target.setStatus((customer.getStatus() == CustomerStatus.ACTIVE) ? TargetStatus.ACTIVE : TargetStatus.INACTIVE);
			target.setScope(customer.getScope());
			target = (Target)targetBean.insert(target);
		} else {
			target.setTariff(customer.getTariff());
			target.setSurcharge(customer.isSurcharge());
			target.setWithholding(customer.isWithholding());
			target.setTransaction(customer.getTransaction());
			target.setSkipUpdateCustomer(true);
			target = (Target)targetBean.update(target);
			target.setSkipUpdateCustomer(false);
		}
		return target;
	}

}
