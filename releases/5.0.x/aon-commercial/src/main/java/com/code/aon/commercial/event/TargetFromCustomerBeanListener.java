package com.code.aon.commercial.event;

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

/**
 * Listener Added to the customerController.
 */
public class TargetFromCustomerBeanListener extends ManagerBeanListenerAdapter {

	/**
	 * After bean inserted. Adds the related target.
	 * 
	 * @param event the event
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		Customer customer = (Customer)event.getTo();

		IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
		Target target = new Target();
		target.setRegistry(customer.getRegistry());
		target.setAdvertising(Advertising.ALLOWED);
		target.setSurcharge(customer.isSurcharge());
		target.setWithholding(customer.isWithholding());
		target.setTransaction(customer.getTransaction());
		target.setStatus((customer.getStatus() == CustomerStatus.ACTIVE) ? TargetStatus.ACTIVE : TargetStatus.INACTIVE);
		targetBean.insert(target);
	}
}
