package com.code.aon.ui.tasDelivery.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the customerController.
 */
public class CustomerTargetListener extends ControllerAdapter {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CustomerTargetListener.class.getName());
	
	/**
	 * After bean added. Adds the related target.
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		try {
			IManagerBean targetBean = BeanManager.getManagerBean(Target.class);
			Target target = new Target();
			target.setRegistry(customer.getRegistry());
			targetBean.insert(target);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting target for customer with id= " + customer.getId(), e);
		}
	}
}
