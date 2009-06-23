package com.code.aon.ui.sales.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.customer.Customer;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ui.customer.controller.CustomerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.FormUtil;

public class CustomerFeeControllerListener extends ControllerAdapter {
	
	private static final String CUSTOMER_CONTROLLER_NAME = "customer";

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CustomerController customerController = (CustomerController)FormUtil.getController(CUSTOMER_CONTROLLER_NAME);
		Customer customer = (Customer)customerController.getTo();
		((CustomerFee)event.getController().getTo()).setCustomer(customer);
		((CustomerFee)event.getController().getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((CustomerFee)event.getController().getTo()).setQuantity(1.0);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CustomerFee customerFee = (CustomerFee)event.getController().getTo();
		validateFeeDates(customerFee);
	}

	private void validateFeeDates(CustomerFee customerFee) throws ControllerListenerException {
		if(customerFee.getFinalDate() != null){
			if(customerFee.getFinalDate().before(customerFee.getInitialDate())){
				throw new ControllerListenerException("Final date can't be earlier than Initial Date");
			}
		}

		Calendar billingCalendar = new GregorianCalendar();
        billingCalendar.setTime(customerFee.getBillingDate());
        int billingMonth = billingCalendar.get(Calendar.MONTH);
        int billingYear = billingCalendar.get(Calendar.YEAR);
        Calendar initialCalendar = new GregorianCalendar();
        initialCalendar.setTime(customerFee.getInitialDate());
        int initialMonth = initialCalendar.get(Calendar.MONTH);
        int initialYear = initialCalendar.get(Calendar.YEAR);
        if((billingYear < initialYear) || (billingYear == initialYear && billingMonth < initialMonth)) {
			throw new ControllerListenerException("Billing date can't be earlier than Initial Date");
		}
    }
}