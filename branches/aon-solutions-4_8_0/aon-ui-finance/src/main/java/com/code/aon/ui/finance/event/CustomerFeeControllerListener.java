package com.code.aon.ui.finance.event;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.CustomerFee;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerFeeControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
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
				throw new ControllerListenerException("Final Date can't be earlier than Initial Date");
			}
		}

		Calendar initialCalendar = new GregorianCalendar();
		initialCalendar.setTime(customerFee.getInitialDate());
		initialCalendar.set(Calendar.DAY_OF_MONTH, 1);
		if(customerFee.getBillingDate().before(initialCalendar.getTime())){
			throw new ControllerListenerException("Billing Date can't be earlier than Initial Date");
		}
	}

}