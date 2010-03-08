package com.code.aon.ui.customer.event.deprecated;

import com.code.aon.customer.Customer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

@Deprecated
public class CustomerNewWindowControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
	    Customer customer = (Customer)event.getController().getTo();
        if (customer.getTariff() != null && customer.getTariff().getId() == null) {
            customer.setTariff(null);
        }
        if (customer.getCustomerSegment() != null && customer.getCustomerSegment().getId() == null) {
            customer.setCustomerSegment(null);
        }
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        Customer customer = (Customer)event.getController().getTo();
        if (customer.getTariff() != null && customer.getTariff().getId() == null) {
            customer.setTariff(null);
        }
        if (customer.getCustomerSegment() != null && customer.getCustomerSegment().getId() == null) {
            customer.setCustomerSegment(null);
        }
	}

}